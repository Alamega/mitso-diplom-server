const game = document.getElementById("game");
const context = game.getContext("2d");

const GLOBAL_HEIGHT = 480; //Высота канваса
const GLOBAL_WIDTH = 720; //Ширина канваса

game.height = GLOBAL_HEIGHT;
game.width = GLOBAL_WIDTH;

//Картиночки
const BlocksImages = [];
BlocksImages.push(new Image());
BlocksImages[0].src = "/images/game/block0.png";
BlocksImages.push(new Image());
BlocksImages[1].src = "/images/game/block1.png";
const CursorImage = new Image();
CursorImage.src = "/images/game/cross.png";
const FireballImage = new Image();
FireballImage.src = "/images/game/fireball.png";

var keyW = false;
var keyA = false;
var keyS = false;
var keyD = false;
var mouseX = GLOBAL_WIDTH / 2;
var mouseY = GLOBAL_HEIGHT / 2;

class Map {
  static All = [
    [
      [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 0, 2, 0, 2, 0, 2, 0, 2, 0, 2, 2, 0, 2, 0, 2, 0, 2, 0, 2, 0, 1],
      [1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
      [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    ],
  ];
  static shiftX = 0;
  static shiftY = 0;
  static currentMap = [];
  static currentMapIndex = 0;
  static init(newMapIndex, heroPosX = 1, heroPosY = 1) {
    Map.currentMapIndex = newMapIndex;
    heroPosX *= Block.SIZE_X;
    heroPosY *= Block.SIZE_Y;
    Map.currentMap = [];
    Enemy.All = [];
    Projectile.All = [];
    for (let y = 0; y < Map.All[Map.currentMapIndex].length; y++) {
      const tempArr = [];
      for (let x = 0; x < Map.All[Map.currentMapIndex][y].length; x++) {
        switch (Map.All[Map.currentMapIndex][y][x]) {
          case 0:
            tempArr.push(new Block(x * Block.SIZE_X, y * Block.SIZE_Y, Block.SIZE_X, Block.SIZE_Y, 0, false));
            break;
          case 1:
            tempArr.push(new Block(x * Block.SIZE_X, y * Block.SIZE_Y, Block.SIZE_X, Block.SIZE_Y, 1, true));
            break;
          case 2:
            tempArr.push(new Block(x * Block.SIZE_X, y * Block.SIZE_Y, Block.SIZE_X, Block.SIZE_Y, 0, false));
            Enemy.All.push(new Enemy(x * Block.SIZE_X, y * Block.SIZE_Y, 1, 40, 40, "/images/game/tarakanus.png", null));
            break;
          default:
            break;
        }
      }
      Map.currentMap.push(tempArr);
    }

    //Позиция героя на новой карте
    Hero.x = heroPosX;
    Hero.y = heroPosY;

    //Это чтобы центр карты был равен позиции героя
    Map.shiftX = Hero.x + Hero.width / 2 - GLOBAL_WIDTH / 2;
    Map.shiftY = Hero.y + Hero.height / 2 - GLOBAL_HEIGHT / 2;
  }

  static draw() {
    for (let y = 0; y < Map.currentMap.length; y++) {
      for (let x = 0; x < Map.currentMap[y].length; x++) {
        context.drawImage(
          BlocksImages[Map.currentMap[y][x].code],
          Map.currentMap[y][x].x - Map.shiftX,
          Map.currentMap[y][x].y - Map.shiftY,
          Map.currentMap[y][x].width,
          Map.currentMap[y][x].height
        );
      }
    }
  }
}

class Block {
  //Ширина и высота обычных блоков
  static SIZE_X = 40;
  static SIZE_Y = 40;
  constructor(xpos, ypos, width, height, code, collision = false) {
    this.x = xpos;
    this.y = ypos;
    this.width = width;
    this.height = height;
    this.code = code;
    this.collision = collision;
  }
}

class Person {
  constructor(xpos, ypos, moveSpeed, width, height, image) {
    this.x = xpos;
    this.y = ypos;
    this.width = width;
    this.height = height;
    this.moveSpeed = Math.round(moveSpeed);
    this.image = new Image();
    this.image.src = image;
    this.kills = 0;
  }
  getCurrentPosBlock() {
    return {
      x: Math.round(this.x / Block.SIZE_X),
      y: Math.round(this.y / Block.SIZE_Y),
    };
  }
}

class PersonHero extends Person {
  move() {
    if (keyW && !getCollision(this).W) {
      this.y -= this.moveSpeed;
      Map.shiftY -= this.moveSpeed;
    }
    if (keyA && !getCollision(this).A) {
      this.x -= this.moveSpeed;
      Map.shiftX -= this.moveSpeed;
    }
    if (keyS && !getCollision(this).S) {
      this.y += this.moveSpeed;
      Map.shiftY += this.moveSpeed;
    }
    if (keyD && !getCollision(this).D) {
      this.x += this.moveSpeed;
      Map.shiftX += this.moveSpeed;
    }
  }
}

const Hero = new PersonHero(Block.SIZE_X * 5, Block.SIZE_Y * 5, 1, 40, 40, "/images/game/Hero.png");

class Enemy extends Person {
  static All = [];

  constructor(xpos, ypos, moveSpeed, width, height, image, direction) {
    super(xpos, ypos, moveSpeed, width, height, image);
    this.direction = direction;
    setInterval(() => {
      if (this) {
        switch (Math.floor(Math.random() * 4) + 1) {
          case 1:
            this.direction = "W";
            break;
          case 2:
            this.direction = "A";
            break;
          case 3:
            this.direction = "S";
            break;
          case 4:
            this.direction = "D";
            break;
          default:
            break;
        }
      }
    }, (Math.random() + 1) * 1000);
  }

  static MoveAll() {
    for (let i = 0; i < Enemy.All.length; i++) {
      switch (Enemy.All[i].direction) {
        case "W":
          if (!getCollision(Enemy.All[i]).W) {
            Enemy.All[i].y -= Enemy.All[i].moveSpeed;
          }
          break;
        case "A":
          if (!getCollision(Enemy.All[i]).A) {
            Enemy.All[i].x -= Enemy.All[i].moveSpeed;
          }
          break;
        case "S":
          if (!getCollision(Enemy.All[i]).S) {
            Enemy.All[i].y += Enemy.All[i].moveSpeed;
          }
          break;
        case "D":
          if (!getCollision(Enemy.All[i]).D) {
            Enemy.All[i].x += Enemy.All[i].moveSpeed;
          }
          break;
        default:
          break;
      }
    }
  }

  static draw() {
    for (let i = 0; i < Enemy.All.length; i++) {
      context.drawImage(Enemy.All[i].image, Enemy.All[i].x - Map.shiftX, Enemy.All[i].y - Map.shiftY, Enemy.All[i].width, Enemy.All[i].height);
    }
  }
}

class Projectile {
  static All = [];

  constructor(shooter, endX, endY, width, height, speed) {
    this.shooter = shooter;
    this.startX = this.shooter.x + this.shooter.width / 2;
    this.startY = this.shooter.y + this.shooter.height / 2;
    this.endX = endX;
    this.endY = endY;
    this.width = width;
    this.height = height;
    this.speed = speed;
    this.x = this.startX - this.width / 2;
    this.y = this.startY - this.height / 2;
  }

  static MoveAll() {
    for (let i = 0; i < this.All.length; i++) {
      let dist = Math.sqrt(Math.pow(Projectile.All[i].endX - Projectile.All[i].startX, 2) + Math.pow(Projectile.All[i].endY - Projectile.All[i].startY, 2));
      Projectile.All[i].x += ((Projectile.All[i].endX - Projectile.All[i].startX) / dist) * Projectile.All[i].speed;
      Projectile.All[i].y += ((Projectile.All[i].endY - Projectile.All[i].startY) / dist) * Projectile.All[i].speed;
      for (let j = 0; j < Enemy.All.length; j++) {
        if (simpleCollision(Projectile.All[i], Enemy.All[j])) {
          Hero.kills++;
          Enemy.All.splice(j, 1);
          Projectile.All.splice(i, 1);
          i--;
          break;
        }
      }
    }
  }
}

document.addEventListener("keydown", (event) => {
  switch (event.code) {
    case "KeyW":
      keyW = true;
      break;
    case "KeyA":
      keyA = true;
      break;
    case "KeyS":
      keyS = true;
      break;
    case "KeyD":
      keyD = true;
      break;
    default:
      break;
  }
});

document.addEventListener("keyup", (event) => {
  switch (event.code) {
    case "KeyW":
      keyW = false;
      break;
    case "KeyA":
      keyA = false;
      break;
    case "KeyS":
      keyS = false;
      break;
    case "KeyD":
      keyD = false;
      break;
    default:
      break;
  }
});

game.addEventListener("mousemove", (event) => {
  mouseX = Math.round((event.offsetX / game.clientWidth) * GLOBAL_WIDTH);
  mouseY = Math.round((event.offsetY / game.clientHeight) * GLOBAL_HEIGHT);
});

game.addEventListener("click", () => {
  Projectile.All.push(new Projectile(Hero, mouseX + Map.shiftX, mouseY + Map.shiftY, 40, 40, 2));
});

//Переключение вкладки останавливает движение
window.onblur = function () {
  keyW = keyA = keyS = keyD = false;
};

//Вспомогательная функция для поиска коллизий
function setElCollision(el1, el2, coll) {
  if (el1.y == el2.y + el2.height && el2.x < el1.x + el1.width && el1.x < el2.x + el2.width) {
    coll.W = true;
  }
  if (el1.x == el2.x + el2.width && el2.y < el1.y + el1.height && el1.y < el2.y + el2.height) {
    coll.A = true;
  }
  if (el1.y + el1.height == el2.y && el2.x < el1.x + el1.width && el1.x < el2.x + el2.width) {
    coll.S = true;
  }
  if (el1.x + el1.width == el2.x && el2.y < el1.y + el1.height && el1.y < el2.y + el2.height) {
    coll.D = true;
  }
}

//Поиск коллизий
function getCollision(person) {
  let coll = {
    W: false,
    A: false,
    S: false,
    D: false,
    any: false,
  };

  //Коллизия с картой
  for (let y = person.getCurrentPosBlock().y - 1; y < Map.currentMap.length && y < person.getCurrentPosBlock().y + person.height / Block.SIZE_Y + 1; y++) {
    if (Map.currentMap[y] != null) {
      for (
        let x = person.getCurrentPosBlock().x - 1;
        x < Map.currentMap[y].length && x < person.getCurrentPosBlock().x + person.width / Block.SIZE_X + 1;
        x++
      ) {
        if (Map.currentMap[y][x] != null && Map.currentMap[y][x].collision) {
          setElCollision(person, Map.currentMap[y][x], coll);
          // Отрисовать все читаемые блоки
          // context.fillStyle = "red";
          // context.fillRect(Map.currentMap[y][x].x - Map.shiftX, Map.currentMap[y][x].y - Map.shiftY, Map.currentMap[y][x].width, Map.currentMap[y][x].height);
        }
      }
    }
  }

  //Коллизия с врагами
  for (let i = 0; i < Enemy.All.length; i++) {
    setElCollision(person, Enemy.All[i], coll);
  }

  //Коллизия с героем
  if (!person.startX) {
    setElCollision(person, Hero, coll);
  }

  coll.any = coll.W || coll.A || coll.S || coll.D;
  return coll;
}

//Простая коллизия
function simpleCollision(el1, el2) {
  return el1.x < el2.x + el2.width && el1.x + el1.width > el2.x && el1.y < el2.y + el2.height && el1.height + el1.y > el2.y;
}

//Начало игры
Map.init(0, 3, 3);

//Игровой цикл
setInterval(() => {
  Hero.move();
  Enemy.MoveAll();
  Projectile.MoveAll();
}, 4);

//Рисовательский цикл
function Draw() {
  context.fillStyle = "rgb(86,57,35)";
  context.fillRect(0, 0, GLOBAL_WIDTH, GLOBAL_HEIGHT);

  Map.draw();
  Enemy.draw();

  //Рисование снарядов
  for (let i = 0; i < Projectile.All.length; i++) {
    context.drawImage(FireballImage, Projectile.All[i].x - Map.shiftX, Projectile.All[i].y - Map.shiftY, Projectile.All[i].width, Projectile.All[i].height);
  }

  //Рисование персонажа
  context.drawImage(Hero.image, GLOBAL_WIDTH / 2 - Hero.width / 2, GLOBAL_HEIGHT / 2 - Hero.height / 2, Hero.width, Hero.height);

  //Рисование курсора
  context.drawImage(CursorImage, mouseX - 20, mouseY - 20, 40, 40);

  context.fillStyle = "white";
  context.font = "32px serif";
  context.fillText("Убито тараканусов: " + Hero.kills, GLOBAL_WIDTH / 2 - 150, 50);

  requestAnimationFrame(Draw);
}

window.onload = Draw;
