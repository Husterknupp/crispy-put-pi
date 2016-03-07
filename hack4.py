#!/usr/bin/python
import re
import requests
import time

order1Items = []
order2Items = []
order3Items = []
box1used, box2used, box3used = False, False, False
order1box, order2box, order3box = None, None, None
url = 'http://raspberry-pi-ip:4567'

def led_on_or_flash(orderItem, orderItems, itemsMax, led):
    if not orderItem in orderItems:
        if len(orderItems) == itemsMax:
            print 'led flash'
            requests.put(url + '/leds/'+ str(led) + '/flash')
            orderItems = []
        else:
            print 'led on.. off'
            requests.put(url + '/leds/'+ str(led) + '/on')
            time.sleep(1.5)
            requests.put(url + '/leds/'+ str(led) + '/off')
            orderItems.append(orderItem)
    else:
        print 'led on.. off'
        requests.put(url + '/leds/'+ str(led) + '/on')
        time.sleep(1.5)
        requests.put(url + '/leds/'+ str(led) + '/off')
    return orderItems

while True:
    orderItem = raw_input("order + item: ")
    if orderItem == 'boxes':
        print "box1used, box2used, box3used"
        print str(box1used) + ", " + str(box2used) + ", " + str(box3used)
        print "order1box, order2box, order3box"
        print str(order1box) + ", " + str(order2box) + ", " + str(order3box)
        continue

    if orderItem == 'tooot':
        i = 0
        while i < 5:
            requests.put(url + '/leds/6/on')
            time.sleep(0.2)
            requests.put(url + '/leds/6/off')
            i = i + 1
        continue

    if orderItem == 'sudo on':
        requests.put(url + '/leds/on')
        continue

    if orderItem == 'off':
        requests.put(url + '/leds/off')
        continue

    pattern = re.compile('order[0-9]', flags=re.IGNORECASE)
    order = pattern.match(orderItem)
    if order == None:
        print 'not my cup of tea'
        i = 0
        while i < 10:
            requests.put(url + '/leds/3/on')
            time.sleep(0.1)
            requests.put(url + '/leds/3/off')
            requests.put(url + '/leds/4/on')
            time.sleep(0.1)
            requests.put(url + '/leds/4/off')
            i = i + 1
        continue

    # ORDER 1
    if order.group(0) == 'order1':
        if len(order1Items) == 0:
            if not box1used:
                order1box = 0
                box1used = True
            elif not box2used:
                order1box = 1
                box2used = True
            else:
                order1box = 2
                box3used = True
        order1Items = led_on_or_flash(orderItem, order1Items, 2, order1box)
        # clean up when all shipped
        if len(order1Items) == 0:
            if order1box == 0:
                box1used = False
            elif order1box == 1:
                box2used = False
            else:
                box3used = False

    # ORDER 2
    elif order.group(0) == 'order2':
        if len(order2Items) == 0:
            if not box1used:
                order2box = 0
                box1used = True
            elif not box2used:
                order2box = 1
                box2used = True
            else:
                order2box = 2
                box3used = True
        order2Items = led_on_or_flash(orderItem, order2Items, 2, order2box)
        # clean up when all shipped
        if len(order2Items) == 0:
            if order2box == 0:
                box1used = False
            elif order2box == 1:
                box2used = False
            else:
                box3used = False

    # ORDER 3
    elif order.group(0) == 'order3':
        if len(order3Items) == 0:
            if not box1used:
                order3box = 0
                box1used = True
            elif not box2used:
                order3box = 1
                box2used = True
            else:
                order3box = 2
                box3used = True
        order3Items = led_on_or_flash(orderItem, order3Items, 1, order3box)
        # clean up when all shipped
        if len(order3Items) == 0:
            if order3box == 0:
                box1used = False
            elif order3box == 1:
                box2used = False
            else:
                box3used = False

    else: print 'za order is unknówn'

    print order1Items
    print order2Items
    print order3Items
