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

    function addFromJSON(root, json) {
        if (!document.getElementById(json.mac)) {
            let newInfo = document.createElement("div");
            newInfo.id = json.mac;
            newInfo.className = "info-one-record";

            //Информация о статусе
            let statusHeader = document.createElement("div");
            statusHeader.id = "status-header";
            statusHeader.className = "info-header";
            let isOnline = document.createElement("p");
            isOnline.innerText = "Online";
            statusHeader.appendChild(isOnline);
            let statusInfo = document.createElement("p");
            statusInfo.innerText = "Всё в норме";
            statusHeader.appendChild(statusInfo);
            newInfo.appendChild(statusHeader);
            newInfo.appendChild(document.createElement("hr"));

            //Информация о пользователе
            let uuidHeader = document.createElement("div");
            uuidHeader.id = "status-header";
            uuidHeader.className = "info-header";
            let macAddress = document.createElement("p");
            macAddress.innerHTML = 'MAC: <a href="/mac/' + json.mac + "\">" + json.mac + "</a>";
            uuidHeader.appendChild(macAddress);
            let username = document.createElement("p");
            username.innerText = "Имя пользователя: " + json.username;
            uuidHeader.appendChild(username);
            newInfo.appendChild(uuidHeader);
            newInfo.appendChild(document.createElement("hr"));

            //Информация о оперативной памяти
            let ramInfo = document.createElement("div");
            ramInfo.id = "info-ram";
            ramInfo.className = "info-content";
            let ramLabel = document.createElement("p");
            ramLabel.innerText = "Оперативная память:";
            ramInfo.appendChild(ramLabel);
            let ramUsage = document.createElement("p");
            ramUsage.innerText = (json.ram.usage || 0).toFixed(2) + "/" + (json.ram.total || 0).toFixed(2) + "ГБ";
            ramInfo.appendChild(ramUsage);
            newInfo.appendChild(ramInfo);

            //Прогресс-бар оперативной памяти
            let ramBar = document.createElement("progress");
            ramBar.id = "info-ram-bar";
            ramBar.max = 100;
            ramBar.value = (json.ram.usage / json.ram.total) * 100;
            newInfo.appendChild(ramBar);
            newInfo.appendChild(document.createElement("hr"));

            //Информация о процессоре
            let cpuInfo = document.createElement("div");
            cpuInfo.id = "info-cpu";
            cpuInfo.className = "info-content";
            let cpuLabel = document.createElement("p");
            cpuLabel.innerText = "Процессор:";
            cpuInfo.appendChild(cpuLabel);
            let cpuUsage = document.createElement("p");
            cpuUsage.innerText = (json.cpuusage || 0) + "%";
            cpuInfo.appendChild(cpuUsage);
            newInfo.appendChild(cpuInfo);

            //Прогресс-бар процессора
            let cpuBar = document.createElement("progress");
            cpuBar.id = "info-cpu-bar";
            cpuBar.max = 100;
            cpuBar.value = json.cpuusage || 0;
            newInfo.appendChild(cpuBar);
            newInfo.appendChild(document.createElement("hr"));

            if (json.gpuinfo.length > 0) {
                //Информация о видеокарте
                let gpuInfo = document.createElement("div");
                gpuInfo.id = "info-gpu";
                gpuInfo.className = "info-content";
                let gpuLabel = document.createElement("p");
                gpuLabel.innerText = "Видеокарта:";
                gpuInfo.appendChild(gpuLabel);
                let gpuUsage = document.createElement("p");
                gpuUsage.innerText = (json.gpuinfo[0].load || 0) + "%";
                gpuInfo.appendChild(gpuUsage);
                newInfo.appendChild(gpuInfo);

                //Прогресс-бар видеокарты
                let gpuBar = document.createElement("progress");
                gpuBar.id = "info-gpu-bar";
                gpuBar.max = 100;
                gpuBar.value = json.gpuinfo[0].load || 0;
                newInfo.appendChild(gpuBar);
                newInfo.appendChild(document.createElement("hr"));
            }

            //Информация о дисках

            let fullDiskUsage = 0;
            let fullDiskTotal = 0;
            json.discs.forEach((disk) => {
                fullDiskUsage += disk.usage;
                fullDiskTotal += disk.total;
            });

            let discsInfo = document.createElement("div");
            discsInfo.id = "info-discs";
            discsInfo.className = "info-content";
            let discsLabel = document.createElement("p");
            discsLabel.innerText = "Общее дисковое пространство:";
            discsInfo.appendChild(discsLabel);
            let discsUsage = document.createElement("p");
            discsUsage.innerText = fullDiskUsage.toFixed(2) + "/" + fullDiskTotal.toFixed(2) + "ГБ";
            discsInfo.appendChild(discsUsage);
            newInfo.appendChild(discsInfo);

            //Прогресс-бар дисков
            let discsBar = document.createElement("progress");
            discsBar.id = "info-discs-bar";
            discsBar.max = 100;
            discsBar.value = (fullDiskUsage / fullDiskTotal) * 100;
            newInfo.appendChild(discsBar);

            root.appendChild(newInfo);
        } else {
            let selectedInfo = document.getElementById(json.mac);
            selectedInfo.querySelectorAll("#info-ram p")[1].innerHTML = (json.ram.usage || 0).toFixed(2) + "/" + (json.ram.total || 0).toFixed(2) + "ГБ";
            selectedInfo.querySelector("#info-ram-bar").value = (json.ram.usage / json.ram.total) * 100;
            selectedInfo.querySelectorAll("#info-cpu p")[1].innerHTML = (json.cpuusage || 0) + "%";
            selectedInfo.querySelector("#info-cpu-bar").value = json.cpuusage || 0;

            if (selectedInfo.querySelector("#info-gpu p")) {
                selectedInfo.querySelectorAll("#info-gpu p")[1].innerHTML = (json.gpuinfo.length > 0 ? json.gpuinfo[0].load : 0) + "%";
                selectedInfo.querySelector("#info-gpu-bar").value = json.gpuinfo.length > 0 ? json.gpuinfo[0].load : 0;
            }

            let fullDiskUsage = 0;
            let fullDiskTotal = 0;
            json.discs.forEach((disk) => {
                fullDiskUsage += disk.usage;
                fullDiskTotal += disk.total;
            });
            selectedInfo.querySelectorAll("#info-discs p")[1].innerHTML = fullDiskUsage.toFixed(2) + "/" + fullDiskTotal.toFixed(2) + "ГБ";
            selectedInfo.querySelector("#info-discs-bar").value = (fullDiskUsage / fullDiskTotal) * 100;
        }
    }

    socket.onmessage = function(event) {
        addFromJSON(document.getElementById("ws-root"), JSON.parse(event.data));
    }

    socket.onclose = function(){
        clearInterval(intervalID);
        setTimeout(function(){start()}, 5000);
    };
}

start();