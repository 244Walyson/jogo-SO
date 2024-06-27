package com.waly.so.game.controllers;

import com.waly.so.game.models.Task;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class WebSocketHandler extends TextWebSocketHandler {

  private List<WebSocketSession> sessions = new ArrayList<>();
  private List<Task> tasks = new ArrayList<>();


  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws IOException {
    System.out.println("Conexão estabelecida: " + session.getId());

    String uriQuery = session.getUri().getQuery();
    String username = uriQuery.split("=")[1].split("&")[0];
    String taskId = uriQuery.split("=")[2];
    System.out.println("Username: " + username);

    sessions.add(session);

    List<Task> filter = tasks.stream().filter(task -> task.getTaskId().equals(taskId)).toList();

    if(filter.isEmpty()){
      tasks.add(new Task(taskId, username, session.getId(), new Thread()));
    }

    tasks.forEach(task -> {
        try {
            session.sendMessage(new TextMessage("Connections Active: " + task.getTaskId() + " - " + task.getUsername()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });


    for (WebSocketSession activeSession : sessions) {
        try {
          if(activeSession.isOpen() && !activeSession.getId().equals(session.getId())){
            activeSession.sendMessage(new TextMessage("New connection: " + session.getId() + " - " + username));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
    }

    session.sendMessage(new TextMessage("Your id is: " + session.getId()));
    tasks.forEach(task -> {
      System.out.println("Task " + task.getTaskId() + " - " + task.getUsername());
    });

  }


  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    String payload = message.getPayload();
    System.out.println("Mensagem recebida de " + session.getId() + ": " + payload);

    if (message.getPayload().contains("start")) {
      sessions.forEach(session1 -> {
        try {
          if(session1.isOpen()){
            session1.sendMessage(new TextMessage("Starting"));
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      try {
        tasks.forEach(Task::startTask);
      } catch (IllegalThreadStateException e) {
        resetThreads();
      }

      threadWinner();
      return;
    }

  }

  private void resetThreads() {
    tasks.forEach(task -> {
      task.setThread(new Thread());
      task.setReady(false);
      task.setStartTime(new AtomicLong(0));
      task.setEndTime(new AtomicLong(0));
      task.setDiffTime(0);
    });
    tasks.forEach(Task::startTask);
  }

  private void threadWinner(){
    while (tasks.stream().anyMatch(task -> !task.getReady())) {}
    threadCounter();
  }
  private void threadCounter() {
    tasks.sort(Comparator.comparing(Task::getDiffTime));
    List<String> ranking = new ArrayList<>();
    for (int i = 0; i < tasks.size(); i++) {
      ranking.add("{ \"username\": \"" + tasks.get(i).getUsername() + "\", \"position\": " + i + ", \"time\": " + tasks.get(i).getDiffTime() + ", \"id\": \"" + tasks.get(0).getTaskId() + "\" }");
    }
     sessions.forEach(session -> {
       try {
         session.sendMessage(new TextMessage("Ranking: $$" + ranking));
       } catch (Exception e) {
         e.printStackTrace();
       }
     });
  }
  @Override
  public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
    sessions = sessions.stream().filter(session1 -> !session1.getId().equals(session.getId())).collect(Collectors.toList());

    sessions.forEach(session1 -> {
      System.out.println(session.getId());
    });

    sessions.forEach(session1 -> {
      try {
        if (session1.isOpen()){
          session1.sendMessage(new TextMessage("Connection closed: " + session.getId() + " - " + tasks.stream().filter(task -> task.getSessionId().equals(session.getId())).findFirst().get().getUsername()));
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}