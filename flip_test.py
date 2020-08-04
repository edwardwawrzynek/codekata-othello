board_string = """
00000000
00000000
00002000
00022000
00012100
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
    (-1, 0), # west
    (-1, -1), # northeast
    (-1, 1), # southeast
    (1, -1), # northwest
    (1, 1) # southwest
]

def get_flipped_tiles(player, x, y):
    opponent = {1: 2, 2: 1}[player]

    flipped_tiles = []

    for direction in directions:
        #print(direction)
        path = []
        
        focus = [x, y]
        first = True
        found_end = False

        while board[focus[0]][focus[1]] == opponent or first:
            first = False

            # north
            focus[0] += direction[0]
            focus[1] += direction[1]
            
            if (not (focus[0] in range(8))) or (not (focus[1] in range(8))): break
            #print(focus)
            if board[focus[0]][focus[1]] == player:
                found_end = True
                #print("End found at:", focus)
                break
            else:
                #print("Adding to path:", focus)
                path.append(focus.copy())
        if found_end:
            flipped_tiles += path


    return flipped_tiles

def flip_tiles(tiles):
    for tile in tiles:
        board[tile[0]][tile[1]] = {1:2,2:1}[board[tile[0]][tile[1]]]

"""
for x in range(8):
    for y in range(8):
        if len(get_flipped_tiles(1, x, y)) > 0:
            print(x, y)
"""
print(get_flipped_tiles(1, 3, 2))
