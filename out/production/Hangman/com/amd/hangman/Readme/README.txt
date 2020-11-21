Networking Project 2

Ashley DelVentura (adelventura3)
- No teammate

HOW TO RUN:
- Compile the code using the Makefile.
    make

TEST RESULT:
- Server output for test result is in file server_readme.txt
- Client output for test result is in file client_readme.txt
- Dictionary used in test result is in dictionary_readme.txt
- For the test, I start connection on three different clients, then also attempt to connect with a 4th.
  This is to better demonstrate what the Server will output.

My implementation follows the one outlined in the project description and project addendum.

STARTUP:
The player starts the game by entering y, n, or a number.
- If the player enters n, then the server will close the connection with a disconnection message
  printed on server side, and then the client will terminate.
- If the player enters y, then the server will start a new game for the player.
    - However, if there are currently 3 other games running (3 other sockets) then the server will send a message saying
      "Server already connected to three clients. Please try again later." and the client will terminate.
      Server will also print connection denied message: "Maximum of 3 connections. Connection request from Socket[addr=/127.0.0.1,port=63942,localport=2017] denied."
- If the player enters a number, then server will start a new game for the player and use the number they entered to
  select the word to guess from the dictionary (backdoor).
    - Same as above if there are already 3 sockets running though.

GAMEPLAY:
- The client will receive a control packet from the server and parse the contents and print them in the following format:
  _ _ _ _
  Incorrect Guesses:

  Letter to guess:
- The player enters a guess and presses enter. The guess is encoded into a packet and sent to the server. If there is a problem
  with the guess, the server will send an error message packet and wait for another guess.
    - Too many characters error example:
      _ _ _ _
      Incorrect Guesses:

      Letter to guess: qwe
      Error! Please guess ONE letter.
      Letter to guess:

    - Not a character error example:
      _ _ _ _
      Incorrect Guesses:

      Letter to guess: %
      Error! Please guess one LETTER.
      Letter to guess:

    - Repeated guess example:
      _ _ _ _
      Incorrect Guesses:

      Letter to guess: u
      _ _ _ _
      Incorrect Guesses: u

      Letter to guess: u
      Repeated guess! Please try again.
      Letter to guess:
- The guess entered didn't

SERVER MESSAGES:
- The server prints a startup message when it starts running: "Hangman Server is Running on port [port number]"
- The server will print a connection message for each client connection it accepts.
  For example: "Received connection: Socket[addr=/127.0.0.1,port=62347,localport=2017]"
- The server will print a disconnection message for each closed client connection.
  For example: "Disconnected connection: Socket[addr=/127.0.0.1,port=62347,localport=2017]"
- If additional clients request connections when there are already 3 connections, the ser

CLIENT MESSAGES:
- During game play, the client only prints messages recieved from the server.
- Messages from the server are:
    - "You win!"
    - "You lose: [word]"
    - "Letter to guess:"
    - "Ready to start game? (y/n):"
    - "Error! Letter [letter] has been guessed before. Please guess another letter."
    - "Error! Please guess one LETTER."
    - "Error! Please guess ONE letter."
    - "Server already connected to three clients. Please try again later."
- Once the game is either won or lost, the client will print "Game over!" and terminate.