local m = peripheral.find("constant_speed_motor")

write("Speed: "..m.getTargetSpeed().."\n")
m.setTargetSpeed(-4.1234)
write("New Speed: "..m.getTargetSpeed().."\n")
