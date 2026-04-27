package experimento;

import grafo.Grafo;
import algoritmos.AlgoritmoBellmanFord;
import algoritmos.AlgoritmoDijkstra;
import ferramentas.LeitorArquivo;
import java.io.*;
import java.util.*;

public class TestadorTempos {
    private static final int NUMERO_EXECUCOES = 10;
    private static final int[] NUMERO_VERTICES = {100, 200, 500};
    private static final double[] DENSIDADES = {0.2, 0.4, 0.6, 0.8, 1.0};

    public static void main(String[] args) {
        if (args.length == 2) {
            executarTesteEspecifico(args);
        } else {
            executarExperimentoCompleto();
        }
    }

    private static void executarTesteEspecifico(String[] args) {
        int numeroGrafo = Integer.parseInt(args[0]);
        int numeroTestes = Integer.parseInt(args[1]);
        
        List<Long> temposBellmanFordLista = new ArrayList<>();
        List<Long> temposBellmanFordMatriz = new ArrayList<>();
        List<Long> temposDijkstraLista = new ArrayList<>();
        List<Long> temposDijkstraMatriz = new ArrayList<>();

        Grafo grafoLista = carregarGrafo(numeroGrafo, false);
        Grafo grafoMatriz = carregarGrafo(numeroGrafo, true);

        for (int i = 0; i < numeroTestes; i++) {
            // Bellman-Ford com Lista
            long inicio = System.nanoTime();
            AlgoritmoBellmanFord.executar(grafoLista, 0);
            long fim = System.nanoTime();
            temposBellmanFordLista.add(fim - inicio);

            // Bellman-Ford com Matriz
            inicio = System.nanoTime();
            AlgoritmoBellmanFord.executar(grafoMatriz, 0);
            fim = System.nanoTime();
            temposBellmanFordMatriz.add(fim - inicio);

            // Dijkstra com Lista
            inicio = System.nanoTime();
            AlgoritmoDijkstra.executar(grafoLista, 0);
            fim = System.nanoTime();
            temposDijkstraLista.add(fim - inicio);

            // Dijkstra com Matriz
            inicio = System.nanoTime();
            AlgoritmoDijkstra.executar(grafoMatriz, 0);
            fim = System.nanoTime();
            temposDijkstraMatriz.add(fim - inicio);
        }

        salvarResultadosCSV(numeroGrafo, temposBellmanFordLista, temposBellmanFordMatriz, 
                           temposDijkstraLista, temposDijkstraMatriz);
    }

    private static void executarExperimentoCompleto() {
        try (PrintWriter escritor = new PrintWriter(new File("resultados_experimento_completo.csv"))) {
            escritor.println("Algoritmo,Representacao,Vertices,Densidade,TempoMedio(ns)");
            
            for (int vertices : NUMERO_VERTICES) {
                for (double densidade : DENSIDADES) {
                    int arestas = calcularNumeroArestas(vertices, densidade);
                    
                    // Testar com lista de adjacência
                    testarConfiguracao(escritor, vertices, arestas, false, densidade);
                    
                    // Testar com matriz de adjacência
                    testarConfiguracao(escritor, vertices, arestas, true, densidade);
                }
            }
            System.out.println("Experimento completo concluído! Verifique resultados_experimento_completo.csv");
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao criar arquivo: " + e.getMessage());
        }
    }

    private static void testarConfiguracao(PrintWriter escritor, int vertices, int arestas, 
                                         boolean usarMatriz, double densidade) {
        Grafo grafo = GeradorGrafos.gerarGrafoAleatorio(vertices, arestas, usarMatriz, true, true);
        
        // Testar Bellman-Ford
        long tempoBF = executarAlgoritmoMultiplasVezes(grafo, "BellmanFord");
        escritor.printf("Bellman-Ford,%s,%d,%.1f,%d\n", 
                       usarMatriz ? "Matriz" : "Lista", vertices, densidade, tempoBF);
        
        // Testar Dijkstra
        long tempoDijkstra = executarAlgoritmoMultiplasVezes(grafo, "Dijkstra");
        escritor.printf("Dijkstra,%s,%d,%.1f,%d\n", 
                       usarMatriz ? "Matriz" : "Lista", vertices, densidade, tempoDijkstra);
    }

    private static long executarAlgoritmoMultiplasVezes(Grafo grafo, String algoritmo) {
        long tempoTotal = 0;
        
        for (int i = 0; i < NUMERO_EXECUCOES; i++) {
            long inicio = System.nanoTime();
            
            switch (algoritmo) {
                case "BellmanFord":
                    AlgoritmoBellmanFord.executar(grafo, 0);
                    break;
                case "Dijkstra":
                    AlgoritmoDijkstra.executar(grafo, 0);
                    break;
            }
            
            long fim = System.nanoTime();
            tempoTotal += (fim - inicio);
        }
        
        return tempoTotal / NUMERO_EXECUCOES;
    }

    private static Grafo carregarGrafo(int numeroGrafo, boolean usarMatriz) {
        String nomeArquivo = obterNomeArquivo(numeroGrafo);
        try {
            return new Grafo(new LeitorArquivo(new File(nomeArquivo)), usarMatriz, true, true);
        } catch (Exception e) {
            System.err.println("Erro ao carregar grafo: " + e.getMessage());
            return null;
        }
    }

    private static String obterNomeArquivo(int numeroGrafo) {
        switch (numeroGrafo) {
            case 1: return "GrafosEntrada/grafo100-1980.gr";
            case 2: return "GrafosEntrada/grafo100-3960.gr";
            case 3: return "GrafosEntrada/grafo100-5940.gr";
            case 4: return "GrafosEntrada/grafo100-7920.gr";
            case 5: return "GrafosEntrada/grafo100-9900.gr";
            case 6: return "GrafosEntrada/grafo200-7960.gr";
            case 7: return "GrafosEntrada/grafo200-15920.gr";
            case 8: return "GrafosEntrada/grafo200-23880.gr";
            case 9: return "GrafosEntrada/grafo200-31840.gr";
            case 10: return "GrafosEntrada/grafo200-39800.gr";
            case 11: return "GrafosEntrada/grafo500-49900.gr";
            case 12: return "GrafosEntrada/grafo500-99800.gr";
            case 13: return "GrafosEntrada/grafo500-149700.gr";
            case 14: return "GrafosEntrada/grafo500-199600.gr";
            case 15: return "GrafosEntrada/grafo500-249500.gr";
            default: return "GrafosEntrada/grafo100-1980.gr";
        }
    }

    private static int calcularNumeroArestas(int vertices, double densidade) {
        return (int) (densidade * vertices * (vertices - 1));
    }

    private static void salvarResultadosCSV(int numeroGrafo, List<Long> temposBFLista, 
                                          List<Long> temposBFMatriz, List<Long> temposDijkstraLista, 
                                          List<Long> temposDijkstraMatriz) {
        String nomeArquivo = "resultados/resultado_grafo_" + numeroGrafo + ".csv";
        
        try (PrintWriter escritor = new PrintWriter(new File(nomeArquivo))) {
            escritor.print("Algoritmo/Representacao");
            for (int i = 0; i < temposBFLista.size(); i++) {
                escritor.print(",Execucao " + (i + 1));
            }
            escritor.println();

            escritor.print("Bellman-Ford (Lista)");
            for (Long tempo : temposBFLista) escritor.print("," + tempo);
            escritor.println();

            escritor.print("Bellman-Ford (Matriz)");
            for (Long tempo : temposBFMatriz) escritor.print("," + tempo);
            escritor.println();

            escritor.print("Dijkstra (Lista)");
            for (Long tempo : temposDijkstraLista) escritor.print("," + tempo);
            escritor.println();

            escritor.print("Dijkstra (Matriz)");
            for (Long tempo : temposDijkstraMatriz) escritor.print("," + tempo);
            escritor.println();

            System.out.println("Resultados salvos em: " + nomeArquivo);
        } catch (FileNotFoundException e) {
            System.err.println("Erro ao salvar resultados: " + e.getMessage());
        }
    }
}