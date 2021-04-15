package com.company;
import java.io.*;
import java.net.*;
import java.util.*;
public class Servidor extends Thread {
    private static Vector clientes;
    private Socket conexao;
    private String meuNome;

    public Servidor(Socket s) {
        conexao = s;
    }

    public static void main(String[] args) throws IOException, EOFException  {
        clientes = new Vector();
        ServerSocket s = new ServerSocket(2000);
        while (true) {
            System.out.print("Esperando conectar...");
            Socket conexao = s.accept();
            System.out.println(" Conectado!");
            Thread t = new Servidor(conexao);
            t.start();
        }
    }

    public void run() {
        BufferedReader entrada = null;
        try {
            entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream saida = null;
        try {
            saida = new PrintStream(conexao.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            meuNome = entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (meuNome == null) {
            return;
        }
        clientes.add(saida);
        String linha = null;
        try {
            linha = entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while ((linha != null) && (!linha.trim().equals(""))) {
            try {
                sendToAll(saida, " disse: ", linha);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                linha = entrada.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            sendToAll(saida, " saiu ", " do Chat!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientes.remove(saida);
        try {
            conexao.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(PrintStream saida, String acao,
                          String linha) throws IOException {
        Enumeration e = clientes.elements();
        while (e.hasMoreElements()) {
            PrintStream chat = (PrintStream) e.nextElement();
            if (chat != saida) {
                chat.println(meuNome + acao + linha);
            }
            if (acao == " saiu ") {
                if (chat == saida)
                    chat.println("");
            }
        }
    }
}