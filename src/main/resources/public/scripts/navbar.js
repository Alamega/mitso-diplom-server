const menu = document.getElementById("navbar_menu");
const navbar = document.getElementById("navbar");
menu.style.display = "none";
function switchNavbar() {
    if (menu.style.display === "none") {
        navbar.style.boxShadow = "0 0 4px black";
        menu.style.display = "flex";
    } else {
        navbar.style.boxShadow = "";
        menu.style.display = "none";
    }
}