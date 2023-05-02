# ProjetoNapster
P2P system that allows the transfer of gigantic video files (more than 1 GB) between peers, intermediated by a centralized server, using TCP and UDP as transport layer protocol. 
The system will work in a similar way (but very reduced) to the Napster system

### 💻 Description
<p>The system will consist of 1 server (with known IP and port) and many peers. the peer
acts both as a provider of information (in this case files) and as a receiver from them.
Initially, the server will be available to receive requests from peers. When one
PeerX enters the system, it must communicate its information to the server. The server will receive
information and store it for future reference. When a PeerY wants to download
a video, you must send a request with the file name to the server. The server
will look up the name and respond to PeerY with a list of peers that contain it. O
PeerY will receive the list from the server and choose one of the peers from the list (let's assume that the
chosen is PeerZ). Next, PeerY will request the file for PeerZ, who can
accept the request by sending the file, or reject the request. Finally, when PeerY
download the file into a folder, the person can go to the folder and view it using a
external playback software such as VLC.</p>

### ⚙️ Features (SERVER)
- [x] Simultaneously receives and responds (with threads) to peer requests
- [x] JOIN request
- [x] LEAVE request
- [x] SEARCH request
- [x] Requisição UPDATE
- [x] Requisição ALIVE
- [x] Requisição UPDATE

### ⚙️ Features (PEER)
- [x] Simultaneously receives and responds (with threads) to requests from the server and other peers
- [x] Sends a JOIN request to the server via UDP
- [x] Send a LEAVE request to the server via UDP
- [x] Sends an UPDATE request to the server via UDP
- [x] Send an ALIVE_OK response to the server via UDP
- [x] Sends a SEARCH request to the server via UDP
- [x] Send a DOWNLOAD request to another peer via TCP

### 🚀 Installation and Prerequisites
<p>This repository contains just Java class that can be compiled and run using JDK 1.8 or later. Before running the class, it is necessary to download and import the GSON library, which is used to serialize and deserialize Java objects to JSON.</p>
## Prerequisites
- [x] JDK 1.8 or later installed on your machine
- [x] GSON library downloaded and imported into your project
## Installation
To run follow these steps:

1. Clone the repository to your local machine
2. Open the project in your preferred IDE
3. Download the GSON library from the official website
4. Import the GSON library into your project
5. Compile and run the Java class

### 🛠 Technologies
<UL>
  <LI>Java Language</LI>
  <LI>A compatible IDE is recommended, such as eclipse for example</LI>
</UL>
