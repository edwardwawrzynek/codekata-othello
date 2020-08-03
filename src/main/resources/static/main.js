var canvas
var ctx
const colors = ["#FFFFFF", "#FF0000", "#0000FF"]

function drawBoard(board, ctx) {
    let cell_size = 32

    // draw axes
    for (var x = 0; x <= cell_size * 8; x += cell_size) {
        ctx.beginPath()
        ctx.moveTo(x, 0)
        ctx.lineTo(x, cell_size * 8)
        ctx.stroke()
    }
    for (var y = 0; y <= cell_size * 8; y += cell_size) {
        ctx.beginPath()
        ctx.moveTo(0, y)
        ctx.lineTo(cell_size * 8, y)
        ctx.stroke()
    }

    board.forEach((col, x) => {
        col.forEach((tile, y) => {
            ctx.fillStyle = colors[tile]
            ctx.beginPath()
            ctx.arc(x * cell_size + cell_size * 0.5, y * cell_size + cell_size * 0.5, cell_size * 0.3, 0, 2 * Math.PI)
            ctx.fill()
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
    window.setInterval(main, 250, ctx)
}