function start(){
    let socket;

    if (location.protocol !== 'https:') {
        socket = new WebSocket("ws://" + window.location.host + "/soloWebSocketInfo")
    } else {
        socket = new WebSocket("wss://" + window.location.host + "/soloWebSocketInfo")
    }

    let intervalID = setInterval(() => {
        if (socket.OPEN) {
            socket.send("ping");
        }
    }, 30000)

    socket.onopen = function(event) {
        socket.send(window.location.pathname.substring(window.location.pathname.lastIndexOf("/")+1));
    }

    socket.onmessage = function(event) {
        let json = JSON.parse(event.data);
        document.getElementById("ws-root").innerText = JSON.stringify(json, null, 2);
    }

    socket.onclose = function(){
        clearInterval(intervalID);
        setTimeout(function(){start()}, 5000);
    };
}
start();