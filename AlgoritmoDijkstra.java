package algoritmos;

import grafo.Grafo;
import java.util.*;

public class AlgoritmoDijkstra {
    
    public static int[] executar(Grafo grafo, int verticeOrigem) {
        int numeroVertices = grafo.getNumeroVertices();
        int[] distancias = new int[numeroVertices];
        boolean[] verticesVisitados = new boolean[numeroVertices];
        Arrays.fill(distancias, Grafo.INFINITO);
        distancias[verticeOrigem] = 0;

        PriorityQueue<int[]> filaPrioridade = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        filaPrioridade.offer(new int[]{verticeOrigem, 0});

        while (!filaPrioridade.isEmpty()) {
            int[] verticeAtual = filaPrioridade.poll();
            int verticeU = verticeAtual[0];

            if (verticesVisitados[verticeU]) continue;
            verticesVisitados[verticeU] = true;

            for (int verticeV : grafo.obterVizinhos(verticeU)) {
                if (!verticesVisitados[verticeV]) {
                    int peso = grafo.obterPeso(verticeU, verticeV);
                    if (distancias[verticeU] != Grafo.INFINITO && 
                        distancias[verticeU] + peso < distancias[verticeV]) {
                        distancias[verticeV] = distancias[verticeU] + peso;
                        filaPrioridade.offer(new int[]{verticeV, distancias[verticeV]});
                    }
                }
            }
        }

        return distancias;
    }

    public static void imprimirDistancias(int[] distancias, int verticeOrigem) {
        System.out.println("Distâncias mínimas a partir do vértice " + verticeOrigem + ":");
        for (int i = 0; i < distancias.length; i++) {
            System.out.println("Vértice " + i + ": " + 
                (distancias[i] == Grafo.INFINITO ? "Infinito" : distancias[i]));
        }
    }
}