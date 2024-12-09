How to run:
`openocd -f SDK1_1_M\ FTDBG.cfg -c "init; reset halt; flash write_image erase /home/ilya/User/ITMO/4-course/itmo-4-course/CSD/Lab2/build/Lab2.hex; reset; exit"`