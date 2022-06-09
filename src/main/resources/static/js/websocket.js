var stompClient = null;

// Function for hide/show elements when a user connects or disconnects from the chatroom
function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility
      = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
    document.getElementById('membersListDiv').style.visibility
      = connected ? 'visible' : 'hidden';
}

// Function for open the socket connection and subscribe to different brokers.
function connect() {
    var socket = new SockJS('/chatroom');
    var user = document.getElementById('from').value;
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });

        stompClient.subscribe('/topic/newMember', function(messageOutput) {
            showNewMember(JSON.parse(messageOutput.body));
        });

        stompClient.subscribe('/topic/disconnectedMember', function(messageOutput) {
            showLeaveMember(JSON.parse(messageOutput.body));
        });

        stompClient.subscribe('/topic/chatroomMembers', function(messageOutput) {
            showMemberList(JSON.parse(messageOutput.body));
        });

        stompClient.subscribe('/user/' + user + '/message', function(messageOutput) {
            showPrivateMessageOutput(JSON.parse(messageOutput.body));
        });

        stompClient.send("/ws/register", {}, JSON.stringify({'memberName':document.getElementById('from').value}));
    });
}

// Function for disconnecting user
function disconnect() {
    if(stompClient != null) {
        // TODO unregister user
        stompClient.send("/ws/unregister", {}, JSON.stringify({'memberName':document.getElementById('from').value}));
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// Function for sending public or private messages
function sendMessage() {
    var from = document.getElementById('from').value;
    var to = document.getElementById('to').value;
    var text = document.getElementById('text').value;

    if (to === "") {
        sendToAll(from, to, text);
    } else {
        sendPrivateMessage(from, to, text);
    }

    // Clear input elements
   document.getElementById('to').value = '';
   document.getElementById('text').value = '';
}

function sendToAll(from, to, text) {
    stompClient.send("/ws/room", {},
          JSON.stringify({'sender':from, 'recipient':to, 'message':text}));
}

function sendPrivateMessage(from, to, text) {
    stompClient.send("/ws/private-room", {},
      JSON.stringify({'sender':from, 'recipient':to, 'message':text}));
}

// Function for print messages to UI
function showMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.style.color = 'blue';
    p.appendChild(document.createTextNode(messageOutput.sender + ": "
      + "(" + messageOutput.timestamp +") " + messageOutput.message));
    response.appendChild(p);
}

function showPrivateMessageOutput(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.style.color = 'red';
    p.appendChild(document.createTextNode(messageOutput.sender + ": "
      + "(" + messageOutput.timestamp +") " + messageOutput.message));
    response.appendChild(p);
}

// Function for updating user for newly joined members
function showNewMember(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode("New member joined: " + messageOutput.memberName));
    response.appendChild(p);
}

// Function for updating user about users left the room.
function showLeaveMember(messageOutput) {
    var response = document.getElementById('response');
    var p = document.createElement('p');
    p.style.wordWrap = 'break-word';
    p.appendChild(document.createTextNode("Member " + messageOutput.memberName + " left chatroom"));
    response.appendChild(p);
}

// Function for keeping updated the list of joined members.
function showMemberList(messageOutput) {
    var response = document.getElementById('membersList');
    response.textContent = '';
    for (i = 0; i < messageOutput.length; i++) {
        var p = document.createElement('p');
        p.style.wordWrap = 'break-word';
        p.appendChild(document.createTextNode(messageOutput[i].memberName));
        response.appendChild(p);
    }
}