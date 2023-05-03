function start(){
    let socket;

    let template = `
          <div class="info-header">
            <p>{isOnline}</p>
            <p>{status}</p>
          </div>
          <hr />
          <div class="info-header">
            <p>MAC: <a href="/mac/{mac}">{mac}</a></p>
            <p>Имя пользователя: {username}</p>
          </div>
          <hr />
          <div class="info-content">
            <p>Оперативная память:</p>
            <p>{memoryUsage}/{memoryTotal}GB</p>
          </div>
          <progress value="0" max="100"></progress>
          <hr />
          <div class="info-content">
            <p>Процессор:</p>
            <p>{cpuPercentage}%</p>
          </div>
          <progress value="0" max="100"></progress>
          <hr />
          <div class="info-content">
            <p>Видеокарта:</p>
            <p>{gpuPercentage}%</p>
          </div>
          <progress value="0" max="100"></progress>
          <hr />
          <div class="info-content">
            <p>Общее дисковое пространство:</p>
            <p>{diskUsage}/{diskTotal}GB</p>
          </div>
          <progress value="0" max="100"></progress>
        `;

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

        let result = template;
        result = result.replaceAll("{isOnline}", "on");
        result = result.replaceAll("{status}", "Всё гуд");
        result = result.replaceAll("{mac}", json.mac);
        result = result.replaceAll("{username}", json.username);

        result = result.replaceAll("{memoryUsage}", json.ram.usage.toFixed(2));
        result = result.replaceAll("{memoryTotal}", json.ram.total.toFixed(2));

        result = result.replaceAll("{cpuPercentage}", json.cpuusage);
        let gpuPersentage = 0;
        if (json.gpuinfo.length > 0) {
            gpuPersentage = json.gpuinfo[0].load;
        }
        result = result.replaceAll("{gpuPercentage}", gpuPersentage);

        let fullDiskUsage = 0;
        let fullDiskTotal = 0;

        json.discs.forEach((disk) => {
            fullDiskUsage += disk.usage;
            fullDiskTotal += disk.total;
        });

        result = result.replaceAll("{diskUsage}", fullDiskUsage.toFixed(2));
        result = result.replaceAll("{diskTotal}", fullDiskTotal.toFixed(2));

        if (document.getElementById(json.mac) == null){
            newEl.innerHTML = result;
            document.getElementById("ws-root").appendChild(newEl);
        } else {
            document.getElementById(json.mac).innerHTML = result;
        }

        let progressBars = document.getElementById(json.mac).getElementsByTagName("progress");
        progressBars[0].value = Math.round((json.ram.usage / json.ram.total) * 100);
        progressBars[1].value = Math.round(json.cpuusage);
        progressBars[2].value = Math.round(gpuPersentage);
        progressBars[3].value = Math.round((fullDiskUsage / fullDiskTotal) * 100);
    }

    socket.onclose = function(){
        clearInterval(intervalID);
        setTimeout(function(){start()}, 5000);
    };
}

start();