var canvas
var ctx
const colors = ["#FFFFFF", "#FF2400", "#87CEEB", "#008000", "#ffbf00"]

function drawBoard(left, top, board, ctx) {
    let cell_size = 32

    // draw axes
    for (var x = 0; x <= cell_size * 8; x += cell_size) {
        ctx.beginPath()
        ctx.moveTo(left + x, top)
        ctx.lineTo(left + x, cell_size * 8 + top)
        ctx.stroke()
    }
    for (var y = 0; y <= cell_size * 8; y += cell_size) {
        ctx.beginPath()
        ctx.moveTo(left, top + y)
        ctx.lineTo(cell_size * 8 + left, top + y)
        ctx.stroke()
    }

    board.forEach((col, x) => {
        col.forEach((tile, y) => {
            ctx.fillStyle = colors[tile]
            ctx.beginPath()
            ctx.arc(left + x * cell_size + cell_size * 0.5, top + y * cell_size + cell_size * 0.5, cell_size * 0.3, 0, 2 * Math.PI)
            ctx.fill()
        })
    })

}

async function main(ctx) {
    const boards_response = await JSON.parse(await (await fetch(`/api/board?key=observe0`)).text());
    const boards = boards_response.boards

    console.log(boards[0])

    for (var i = 0; i < boards.length; ++i) {
        drawBoard(i * 32 * 9, 0, boards[i], ctx)
    }
}

window.onload = () => {
    canvas = document.getElementById("canvas")
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    ctx = canvas.getContext("2d")
    window.setInterval(main, 250, ctx)
}