function start(){
    let socket;

    if (location.protocol !== 'https:') {
        socket = new WebSocket("ws://" + window.location.host + "/webSocketInfo")
    } else {
        socket = new WebSocket("wss://" + window.location.host + "/webSocketInfo")
    }

    let intervalID = setInterval(() => {
        if (socket.OPEN) {
            socket.send("ping");
        }
    }, 30000)

    socket.onmessage = function(event) {
        let json = JSON.parse(event.data);

        let newEl = document.createElement("div");
        newEl.id = json.mac;
        newEl.className = "info-one-record";

        // let result = "";
        // result += "<p>MAC адрес: " + mac + "</p>";
        // result += "<p>Операционная система: " + os + "</p>";
        // result += "<p>Имя пользователя: " + username + "</p>";
        // result += "<p>Процессор " + cpuname + " количество ядер: " + cores + "</p>";
        // result += "<p>Нагрузка на процессор: " + cpuusage + "%</p>";
        //
        // if (Object.keys(cputemp).length > 0 && cputemp[Object.keys(cputemp)[0]] != 0) {
        //     let cputempMap = new Map();
        //     for (let i = 0; i < Object.keys(cputemp).length; i++) {
        //         cputempMap.set(Object.keys(cputemp)[i], cputemp[Object.keys(cputemp)[i]]);
        //     }
        //     cputempMap = new Map([...cputempMap.entries()].sort());
        //     result += "<p>Температура процессора:</p>";
        //     cputempMap.forEach((value, key) => {
        //         result += "<p>" + key + ": " + value + " С</p>";
        //     });
        // }
        //
        // result += "<p>Нагрузка на оперативную память: " + ram + "ГБ</p>";
        // result += "<p>Загруженность на дисках:</p>";
        // for (let i = 0; i < Object.keys(drivers).length; i++) {
        //     result += "<p>Диск " + Object.keys(drivers)[i].substring(0, Object.keys(drivers)[i].length - 2) + " нагрузка: " + drivers[Object.keys(drivers)[i]] + "</p>";
        // }

        //Временно
        newEl.innerText = JSON.stringify(json, null, 2);
        //newEl.innerHTML = result;

        if (document.getElementById(json.mac) == null){
            document.getElementById("ws-root").appendChild(newEl);
        } else {
            //document.getElementById(json.mac).innerHTML = result;
            document.getElementById(json.mac).innerText = JSON.stringify(json, null, 2);
        }
    }

    socket.onclose = function(){
        clearInterval(intervalID);
        setTimeout(function(){start()}, 5000);
    };
}

start();