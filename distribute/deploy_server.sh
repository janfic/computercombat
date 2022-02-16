git clone https://github.com/janfic/computercombat.git
cd computercombat/computercombat
gradle server:dist --no-daemon
cd server/build/libs
java -jar server-1.0.jar