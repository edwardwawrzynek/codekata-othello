#API Description

`GET /api/board - params(key: String)`

Get the current board state. Server uses player 
keys to determine which tiles should be labeled
as ours and which should be labeled as the 
opponent's.

`POST /api/set_name - params(key: String, name: String)`

Set your AI's name on the frontend.

`POST /api/move - params(key: String, x: Int, y: Int)`

Place a marker at position (x, y).