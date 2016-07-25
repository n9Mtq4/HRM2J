-- EXTENDED HUMAN RESOURCE MACHINE PROGRAM --
-- Prints lowercase a to z --

setup:				 -- the setup: sets up the floor
	load 1073741920	 -- the value of 'a' minus 1
	copyto 0			 -- copy it to the first floor space
loop:				 -- the loop: add one to the letter, output, check if done
	inc 0			 -- add one to the letter, and load it into hand
	output			 -- output the letter
	load z			 -- 1/2 if the letter is z, goto the end
	jumpeq 0 end		 -- 2/2 if the letter is z, goto the end
	jump loop		 -- repeat this process 
end:					 -- the end: the place to goto if done
	-- crash			 -- comment/uncomment this to get a stacktrace of the java when the program finishes
