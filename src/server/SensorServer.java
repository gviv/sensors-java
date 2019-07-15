package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Serveur recevant les donn�es des capteurs.
 */
public class SensorServer implements Runnable {

  /**
   * Port de connexion.
   */
  private int port;

  /**
   * Instance de IWriter qui va r�cup�rer le message du serveur.
   */
  private IWriter writer;

  /**
   * Constructeur.
   *
   * @param port   le port de connexion
   * @param writer l'instance de IWriter
   */
  public SensorServer(int port, IWriter writer) {
    this.port = port;
    this.writer = writer;
  }

  /**
   * Lance le serveur.
   */
  @Override
  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      try {
        while (true) {
          Socket socket = serverSocket.accept();
          // Nouveau capteur connect�
          Thread t = new Thread(new SocketReader(socket));
          t.start();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * G�re la r�ception des donn�es � partir d'un socket de capteur.
   */
  private class SocketReader implements Runnable {

    /**
     * Le socket.
     */
    private Socket socket;

    /**
     * Constructeur.
     *
     * @param socket le socket
     */
    public SocketReader(Socket socket) {
      this.socket = socket;
    }

    /**
     * R�ceptionne les message du socket.
     */
    @Override
    public void run() {
      try {
        boolean error = false;
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message = "";
        while (!error && message != null) {
          try {
            message = in.readLine();
            // Nouveau message re�u, on l'�crit dans le writer
            if (message != null) writer.write(message);
          } catch (SocketException e) {
            error = true;
          }
        }

        // Une erreur est survenue ou la fin des message a �t� atteinte,
        // on ferme le BufferedReader et le socket
        in.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

}
