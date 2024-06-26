package com.waly.so.game.services;

import java.util.ArrayList;
import java.util.List;

public class FleuryAlgorithm {
    private int vertices;  // Número de vértices
    private List<Integer>[] adj; // Lista de adjacência

    // Construtor padrão
    public FleuryAlgorithm() {
    }

    // Construtor com número de vértices especificado
    public FleuryAlgorithm(int numVertices) {
        this.vertices = numVertices;
        adj = new ArrayList[vertices];
        for (int i = 0; i < vertices; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    // Adiciona uma aresta ao grafo
    public void addEdge(int u, int v) {
        adj[u].add(v);
        adj[v].add(u);
    }

    // Remove uma aresta do grafo
    private void removeEdge(int u, int v) {
        adj[u].remove((Integer) v);
        adj[v].remove((Integer) u);
    }

    // Gera um grafo circular Euleriano com 'n' vértices
    public static FleuryAlgorithm generateCircularEulerianGraph(int n) {
        FleuryAlgorithm graph = new FleuryAlgorithm(n);
        for (int i = 0; i < n; i++) {
            graph.addEdge(i, (i + 1) % n); // Conecta i ao próximo vértice (circular)
            graph.addEdge(i, (i - 1 + n) % n); // Conecta i ao vértice anterior (circular)
        }
        return graph;
    }

    // Encontra o caminho Euleriano usando o Algoritmo de Fleury
    public void executeFleury() {
        // Encontra um vértice com grau ímpar
        int u = 0;
        for (int i = 0; i < vertices; i++) {
            if (adj[i].size() % 2 == 1) {
                u = i;
                break;
            }
        }

        // Imprime o caminho Euleriano começando do vértice encontrado
        try {
            printEulerUtil(u);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    // Função recursiva para imprimir o caminho Euleriano a partir do vértice 'u'
    private void printEulerUtil(int u) throws InterruptedException {
        // Recorre por todos os vértices adjacentes a 'u'
        for (int i = 0; i < adj[u].size(); i++) {
            int v = adj[u].get(i);

            // Verifica se a aresta (u, v) é uma ponte
            if (isValidNextEdge(u, v)) {
                System.out.print(u + "-" + v + " ");
                removeEdge(u, v);
                Thread.sleep(0, 700); // Atraso de 0 milissegundos e 500 nanossegundos
                printEulerUtil(v);
            }
        }
    }

    // Verifica se a aresta (u, v) é uma ponte
    private boolean isValidNextEdge(int u, int v) {
        // Caso único: se (u, v) é a única aresta conectando u
        if (adj[u].size() == 1) {
            return true;
        }

        // Número de vértices alcançáveis a partir de u
        boolean[] visited = new boolean[vertices];
        int count1 = dfsCount(u, visited);

        // Remove a aresta (u, v) e faz a contagem novamente
        removeEdge(u, v);
        visited = new boolean[vertices];
        int count2 = dfsCount(u, visited);

        // Adiciona a aresta (u, v) de volta ao grafo
        addEdge(u, v);

        // Uma aresta é uma ponte se count1 > count2
        return count1 <= count2;
    }

    // Conta o número de vértices alcançáveis a partir de v usando DFS
    private int dfsCount(int v, boolean[] visited) {
        visited[v] = true;
        int count = 1;
        for (int adj : adj[v]) {
            if (!visited[adj]) {
                count += dfsCount(adj, visited);
            }
        }
        return count;
    }
}
