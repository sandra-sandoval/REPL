package edu.brown.cs.student.main;

import static spark.Spark.after;

import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Handlers.ViewCSVHandler;
import edu.brown.cs.student.main.Handlers.SearchCSVHandler;
import spark.Filter;
import spark.Spark;
import java.util.HashSet;
import java.util.Set;

/**
 * acts as the server for CSVs and APIs
 */
public class Server {

  static final int port = 1318;

  /**
   * sets the port, filename and headers variable. Uses Spark
   */
  public Server(){
    //Set<Soup> menu = new HashSet<>();
    Spark.port(this.port);
    String file = new String();
    String headers = new String();
        /*
            Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
            be able to make requests to the server.

            By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
            This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
            from any website. Instead, you should set this header to the origin of your client, or a list of origins
            that you trust.

            By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
            Again, it's generally better to be more specific here and only allow the methods you need, but for
            this demo we'll allow all methods.

            We recommend you learn more about CORS with these resources:
                - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
                - https://portswigger.net/web-security/cors
         */
    after((request, response) -> {
      response.header("Access-Control-Allow-Origin", "*");
      response.header("Access-Control-Allow-Methods", "*");
    });

    LoadCSVHandler loadcsv = new LoadCSVHandler();
    // Setting up the handler for the GET /order and /mock endpoints
    //System.out.println("server setup done");
    Spark.get("loadcsv", loadcsv);
    //System.out.println("load setup done");
    Spark.get("viewcsv", new ViewCSVHandler(loadcsv));
    //System.out.println("view setup done");
    Spark.get("searchcsv", new SearchCSVHandler(loadcsv));
    //System.out.println("search setup done");
    Spark.get("broadband", new BroadbandHandler());

    Spark.init();
    //System.out.println("all done");
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?


  }

  /**
   * Executes server
   * @param args
   */
  public static void main(String[] args) {
    Server serve = new Server();
    System.out.println("Server started at http://localhost:" + port);


  }


}


