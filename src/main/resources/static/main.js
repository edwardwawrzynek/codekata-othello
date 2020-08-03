var canvas
var ctx
const colors = ["#CCCCCC", "#FF0000", "#0000FF"]

function drawBoard(board, ctx) {
    // x and y are the top left of the board
    let size = 32
    board.forEach((col, x) => {
        col.forEach((tile, y) => {
            ctx.fillStyle = colors[tile]
            ctx.fillRect(x * size, y * size, size, size)
        })
    })

}

async function main(ctx) {
    const boards = await JSON.parse(await (await fetch(`/api/board?key=observe0`)).text());
    drawBoard(boards.boards[0], ctx)
}

window.onload = () => {
    canvas = document.getElementById("canvas")
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    ctx = canvas.getContext("2d")
    window.setInterval(main, 500, ctx);
}