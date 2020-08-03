board_string = """
00000000
00010000
00020000
00020000
00020000
00000000
00000000
00000000
"""
board = [[0 for i in range(8)] for i in range(8)]
for y, line in enumerate(filter(lambda l: len(l) == 8, board_string.split("\n"))):
    for x, char in enumerate(line):
        board[x][y] = int(char)

directions = [
    (0, -1), # north
    (0, 1), # south
    (1, 0), # east
    (-1, 0) # west
]

def get_flipped_tiles(player, x, y):
    opponent = {1: 2, 2: 1}[player]

    flipped_tiles = []

    for direction in directions:
        print(direction)
        path = []
        
        focus = [x, y]
        first = True
        found_end = False

        while board[focus[0]][focus[1]] == opponent or first:
            first = False

            # north
            focus[0] += direction[0]
            focus[1] += direction[1]
            
            if not (x in range(8)) or not (y in range(8)): break

            if board[focus[0]][focus[1]] == player:
                found_end = True
                print("End found at:", focus)
                break
            else:
                print("Adding to path:", focus)
                path.append(focus.copy())
        if found_end:
            flipped_tiles += path


    return flipped_tiles

tiles = get_flipped_tiles(1, 3, 5)
