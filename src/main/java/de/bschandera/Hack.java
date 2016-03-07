package de.bschandera;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static spark.Spark.get;
import static spark.Spark.put;

public class Hack {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Hello world!");

        final GpioController gpio = GpioFactory.getInstance();
        GpioPinDigitalOutput led00 = provisionNewOutput(RaspiPin.GPIO_00, gpio);
        GpioPinDigitalOutput led01 = provisionNewOutput(RaspiPin.GPIO_01, gpio);
        GpioPinDigitalOutput led02 = provisionNewOutput(RaspiPin.GPIO_02, gpio);
        GpioPinDigitalOutput led03 = provisionNewOutput(RaspiPin.GPIO_03, gpio);
        GpioPinDigitalOutput led04 = provisionNewOutput(RaspiPin.GPIO_04, gpio);
        GpioPinDigitalOutput led05 = provisionNewOutput(RaspiPin.GPIO_05, gpio);
        GpioPinDigitalOutput led06 = provisionNewOutput(RaspiPin.GPIO_06, gpio);
        List<GpioPinDigitalOutput> leds = Arrays.asList(led00, led01, led02, led03, led04, led05, led06);
        Map<Integer, GpioPinDigitalOutput> pinsAsMap = leds.stream()
                .collect(Collectors.toMap(t -> t.getPin().getAddress(), Function.<GpioPinDigitalOutput>identity()));
        List<Integer> pinIds = leds.stream().map(led -> led.getPin().getAddress()).collect(Collectors.toList());

        /*
            ****************
            * HTTP ENDPOINTS
            ****************
         */
        get("/leds", (req, res) -> leds.stream()
                .map(led -> led.getPin() + ": " + led.getState()).collect(Collectors.toList()));

        put("/leds/on", (req, res) -> {
            leds.stream().forEach(led -> led.setState(PinState.HIGH));
            return "all on";
        });

        put("/leds/off", (req, res) -> {
            leds.stream().forEach(led -> led.setState(PinState.LOW));
            return "all off";
        });

        put("/leds/:id/on", (req, res) -> {
            System.out.println("/leds/:id/on");
            int address = Integer.parseInt(req.params(":id"));
            if (!pinIds.contains(address)) {
                res.status(404);
                return "led " + address + " not found\n";
            }

            GpioPinDigitalOutput led = pinsAsMap.get(address);
            if (led.getState().isLow()) {
                led.toggle();
                return "toggled to ON\n";
            } else {
                return "already ON\n";
            }
        });

        put("/leds/:id/off", (req, res) -> {
            System.out.println("/leds/:id/off");
            int address = Integer.parseInt(req.params(":id"));
            if (!pinIds.contains(address)) {
                res.status(404);
                return "led " + address + " not found\n";
            }

            GpioPinDigitalOutput led = pinsAsMap.get(address);
            if (led.getState().isHigh()) {
                led.toggle();
                return "toggled to OFF\n";
            } else {
                return "already OFF\n";
            }
        });

        put("/leds/:id/flash", (req, res) -> {
            System.out.println("/leds/:id/flash");
            int address = Integer.parseInt(req.params(":id"));
            if (!pinIds.contains(address)) {
                res.status(404);
                return "led " + address + " not found\n";
            }

            GpioPinDigitalOutput led = pinsAsMap.get(address);
            for (int i = 0; i < 10; i++) {
                led.toggle();
                Thread.sleep(500);
            }
            return "flashy\n";
        });
    }

    private static GpioPinDigitalOutput provisionNewOutput(Pin pin, GpioController gpio) {
        GpioPinDigitalOutput result = gpio.provisionDigitalOutputPin(pin, PinState.LOW);
        result.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        return result;
    }

}
