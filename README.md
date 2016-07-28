# HRM2J
### Compile Human Resource Machine code into Java.

## About
This provides many ways to run HRM assembly. Compile or interpret.
This is not for testing human resource machine solutions, but instead for running them.
HRM2J does not provide any errors if your code does not work (hrm will complain that your number is outside -999 to 999).
HRM2J can store numbers in the range of -1073741825 to 1073741823 before things start to get weird when your numbers turn into characters instead.
HRM2J will not complain if your numbers are out of range, or you are trying to IO to a box that doesn't exist.

## Why should I use this instead of other interpreters?
HRM2J does not interpret the hrm assembly, instead it converts it into java. This gives a huge speed improvement.

|Program|Time(ms/1000 runs)|
|-----|-----|
|HRM2J (Fast)|76|
|HRM2J (Interpret)|97|
|HRM2J (Bytecode NYI)|???|
|[hrmsandbox](https://github.com/sixlettervariables/hrmsandbox)|602|

Benchmark was performed with [this code (for level 41)](https://github.com/atesgoral/hrm-solutions/blob/9c92d7137f6a7593ab35389ab284fd3dcebd2a74/solutions/41-Sorting-Floor-34.714/20.651.selection-sniperrifle2004.asm),
being measured on 1000 runs. The measure was in running only, parsing the input data. The input data consisted of 61 characters:
```
1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0
```

## The GUI
The gui is still in development, and isn't very user-friendly, but is gets the job done.
In the code area, there is makeshift syntax highlighting as well as autocompletion.
The input area, the floor data must be entered as two entries per line.
For example if you want spot 0 to have a 1, spot 1 to have a 10 and spot 2 to have an 'a' do this:
```
0 1
1 10
2 a
```
The input data is entered as a long string separated by commas. For example
```
1,2,3,4,a,b,c
```
In both of these you can use letters and numbers and we can guess what data type it is without
quotes. If you specifically want a character (like the char 1, not the number), you can surround it with
single quotes like '1'. The input data could consist of 
```
1,'1'
```
Outboxing both will yield the same output, but try adding them, and you will get 1073741874, instead of 2.

### Planned
The following diagram is a plan for the graphical interface.
![planned gui](readme_data/planned.png)

### Current
The following is a screenshot from the GUI in development.
![current gui](readme_data/current.png)

## The Future
There are some plans on what this project is going to turn into.

  1. Expanding the HRM assembly language
    - Improve the language by adding more commands.
    - This already has an experimental 'load' command, which can load any value into your hand.
    - Includes a 'jumpeq' and 'crash' command now as well
  2. Interpret the language
   - In-depth debugging and running the code without compiling.
  3. Compiling to Bytecode
   - This will give huge speed improvements.
   - We will write a .class file without the creation .java files.
   - We can use Bytecode's goto command, rather than having to have methods that are called.
   This will also prevent stack overflow errors with *very* large hrm programs.
