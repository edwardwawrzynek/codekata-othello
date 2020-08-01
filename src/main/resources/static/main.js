
async function main() {
    const board = await JSON.parse(await (await fetch(`/api/board?key=observe0`)).text());
    console.log(board)
}

window.setInterval(main, 500);