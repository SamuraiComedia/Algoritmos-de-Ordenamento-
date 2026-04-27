package algoritmos;

import grafo.Grafo;
import java.util.Arrays;

public class AlgoritmoBellmanFord {
    
    public static int[] executar(Grafo grafo, int verticeOrigem) {
        int numeroVertices = grafo.getNumeroVertices();
        int[] distancias = new int[numeroVertices];
        Arrays.fill(distancias, Grafo.INFINITO);
        distancias[verticeOrigem] = 0;

        // Relaxamento das arestas (V-1) vezes
        for (int i = 1; i < numeroVertices; i++) {
            for (int verticeU = 0; verticeU < numeroVertices; verticeU++) {
                for (int verticeV : grafo.obterVizinhos(verticeU)) {
                    int peso = grafo.obterPeso(verticeU, verticeV);
                    if (distancias[verticeU] != Grafo.INFINITO && 
                        distancias[verticeU] + peso < distancias[verticeV]) {
                        distancias[verticeV] = distancias[verticeU] + peso;
                    }
                }
            }
        }

        // Verificação de ciclos de peso negativo
        for (int verticeU = 0; verticeU < numeroVertices; verticeU++) {
            for (int verticeV : grafo.obterVizinhos(verticeU)) {
                int peso = grafo.obterPeso(verticeU, verticeV);
                if (distancias[verticeU] != Grafo.INFINITO && 
                    distancias[verticeU] + peso < distancias[verticeV]) {
                    System.out.println("ALERTA: Ciclo de peso negativo detectado!");
                    return null;
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