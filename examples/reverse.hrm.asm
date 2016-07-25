-- EXTENDED HUMAN RESOURCE MACHINE PROGRAM --
-- reverses the inputed zero terminated string --
-- https://raw.githubusercontent.com/atesgoral/hrm-solutions/master/solutions/31-String-Reverse-11.122/10.121-FireGoblin.asm --

setup:
	load 0
	copyto 63
part1:
	inc 63
	input
	jumpz part2
	copyto [63]
	jump part1
part2:
	dec 63
	jumpz part1
	copyfrom [63]
	output
	jump part2
