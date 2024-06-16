package com.waly.so.game.services;

import com.waly.so.game.models.Process;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OperatingSystemService {
    private List<Process> processes;
    private AtomicInteger currentId;

    public OperatingSystemService() {
        this.processes = new CopyOnWriteArrayList<>();
        this.currentId = new AtomicInteger(1);
    }

    public Flux<Process> getProcesses() {
        return Flux.fromIterable(processes);
    }

    public Mono<Void> createProcess() {
        Process newProcess = new Process(currentId.getAndIncrement(), "Ready");
        processes.add(newProcess);
        return Mono.empty();
    }

    public Mono<Void> executeProcess() {
        return Mono.fromRunnable(() -> {
            processes.stream()
                    .filter(p -> "Ready".equalsIgnoreCase(p.getState()))
                    .findAny()
                    .ifPresent(process -> {
                        process.setState("Running");
                        // Simulate execution time
                        try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
                        process.setState("Ready");
                    });
        });
    }

    public Mono<Void> blockProcess() {
        return Mono.fromRunnable(() -> {
            processes.stream()
                    .filter(p -> "Ready".equalsIgnoreCase(p.getState()))
                    .findAny()
                    .ifPresent(process -> process.setState("Blocked"));
        });
    }

    public Mono<Void> releaseProcess() {
        return Mono.fromRunnable(() -> {
            processes.stream()
                    .filter(p -> "Blocked".equalsIgnoreCase(p.getState()))
                    .findAny()
                    .ifPresent(process -> process.setState("Ready"));
        });
    }
}
