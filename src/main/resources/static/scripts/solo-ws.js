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
        let { cpuusage, cores, os, gpuinfo, discs, discsphysical, mac, username, ram, cpuinfo, isonline, status} = JSON.parse(event.data);
        console.log(JSON.parse(event.data))
        if (status && status.length > 0) {
            document.getElementById("status").innerText = status;
            document.getElementById("status").style.display = "block";
        } else {
            document.getElementById("status").innerText = "";
            document.getElementById("status").style.display = "none";
        }
        if (isonline) {
            document.getElementById("isonline").innerText = "Online";
            document.getElementById("isonline").style.color = "green";
            document.getElementById("dynamicinfowrapper").style.display = "flex";
        } else {
            document.getElementById("isonline").innerText = "Offline";
            document.getElementById("isonline").style.color = "red";
            document.getElementById("dynamicinfowrapper").style.display = "none";
        }
        document.getElementById("ramusage").innerText = ram.usage.toFixed(2);
        document.getElementById("ramtotal").innerText = ram.total.toFixed(2);
        document.getElementById("rampercentage").value = ram.usage / ram.total * 100;

        document.getElementById("cpuusage").innerText = cpuusage;
        document.getElementById("cpupercentage").value = cpuusage;

        let fulldiskusage = 0
        let fulldisktotal = 0
        discs.forEach(disc => {
            fulldiskusage += disc.usage;
            fulldisktotal += disc.total;
        })

        document.getElementById("fulldiskusage").innerText = fulldiskusage.toFixed(2);
        document.getElementById("fulldisktotal").innerText = fulldisktotal.toFixed(2);
        document.getElementById("fulldiskpercentage").value = fulldiskusage / fulldisktotal * 100;

        for (let i = 0; i < cpuinfo.length; i++) {
            for (let j = 0; j < cpuinfo[i].cores.length; j++) {
                document.getElementById("cpu" + i + "core" + j + "load").innerText = cpuinfo[i].cores[j].load.toFixed(0);
                document.getElementById("barcpu" + i + "core" + j + "load").value = cpuinfo[i].cores[j].load;
                document.getElementById("cpu" + i + "core" + j + "temp").innerText = cpuinfo[i].cores[j].temperature || 0;
                document.getElementById("barcpu" + i + "core" + j + "temp").value = cpuinfo[i].cores[j].temperature || 0;
            }
        }

        for (let i = 0; i < gpuinfo.length; i++) {
            document.getElementById("gpu" + i + "load").innerText = gpuinfo[i].load.toFixed(0);
            document.getElementById("bargpu" + i + "load").value = gpuinfo[i].load;
            document.getElementById("gpu" + i + "temp").innerText = gpuinfo[i].temperature || 0;
            document.getElementById("bargpu" + i + "temp").value = gpuinfo[i].temperature || 0;
        }

        for (let i = 0; i < discsphysical.length; i++) {
            document.getElementById("phys" + i).innerText = discsphysical[i].load.toFixed(0);
            document.getElementById("barphys" + i).value = discsphysical[i].load;
        }

        for (let i = 0; i < discs.length; i++) {
            document.getElementById("logic" + i).innerText = discs[i].usage.toFixed(2) + "/" + discs[i].total.toFixed(2) + "ГБ";
            document.getElementById("barlogic" + i).value = discs[i].usage / discs[i].total * 100;
            document.getElementById("logic" + i + "percentage").innerText = (discs[i].usage / discs[i].total * 100).toFixed(2).toString();
        }
    }

    socket.onclose = function(){
        clearInterval(intervalID);
        setTimeout(function(){start()}, 5000);
    };
}
start();