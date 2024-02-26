# Encryptix
# Check it out live! Links are available here : http://6v4gddjepi6gu6khtkheqkniza2p2u6lsmwa5acod4xq5jh3dkdyevad.onion/
This will be the backend for my messaging service where users may create accounts with a unique username and a password,
once the user logs in they are able to change account information and more importantly they are able to upload a public key (must be unique),
once their public key has been uploaded other  users are able to send that user messages which will be recieved in the form of binary 
gpg file which will then be able to be downloaded to their host device on the front end once that is built. The idea is that
the application , nor anyone else except the recipient , can decrypt messages sent over this service. Privacy and security 
are the top priorities behind the thought process. I can't decrypt it, if someone got access to the database they could not decrypt anything either.

List of endpoints for visualization:


To build a docker image in IntelliJ you can simply click publishImageToLocalRegistry in gradle side bar
![image](https://github.com/justmedusty/Encryptix/assets/87884059/4220ea61-275b-4fa5-be6a-9e0d80dbfad7)


If you get an auth error, go into your Users/your_user/.docker/config.json and remove the "credsStore" : someValue mapping and it will work. Every time you open docker it seems to put it back but getting rid of it fixes that issue.

Once you have built the container you can open up your CLI of choice and type "docker run --name encryptix  -p 6969:6969 -e POSTGRES_PASSWORD=your_password -e POSTGRES_URL=jdbc:postgresql://your_postgres_url:5432/postgres -e POSTGRES_USER=your_user -e JWT_SECRET=yoursecret ktor-docker-image"
