# Encryptix
This will be the backend for my messaging service where users may create accounts with a unique username and a password,
once the user logs in they are able to change account information and more importantly they are able to upload a public key (must be unique),
once their public key has been uploaded other  users are able to send that user messages which will be recieved in the form of binary 
gpg file which will then be able to be downloaded to their host device on the front end once that is built. The idea is that
the application , nor anyone else except the recipient , can decrypt messages sent over this service. Privacy and security 
are the top priorities behind the thought process. I can't decrypt it, if someone got access to the database they could not decrypt anything either.

List of endpoints for visualization:
![image](https://github.com/justmedusty/Encryptix/assets/87884059/9c263f5d-442b-41fc-8771-e0d90e3cc8b4)

