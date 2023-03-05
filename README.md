## Отборочное задание - VK 2023 <br>
## Clock View

### Custom attributes

        <attr name="clockRadius" format="dimension" />
        <!--Clock Dial color & stroke width -->
        <attr name="clockDialColor" format="color" />
        <attr name="clockDialStrokeWidth" format="float" />
        <!--Type of clock number: by default set  -->
        <attr name="clockTypeOfNumbers" format="enum">
            <enum name="arabicNumbers" value="0" />
            <enum name="romanNumbers" value="1" />
        </attr>

        <!--Hour hand color & stroke width -->
        <attr name="clockHourHandColor" format="color" />
        <attr name="clockHourHandStrokeWidth" format="float" />

        <!--Minute hand color & stroke width -->
        <attr name="clockMinuteHandColor" format="color" />
        <attr name="clockMinuteHandStrokeWidth" format="float" />

        <!--Second hand color & stroke width -->
        <attr name="clockSecondHandColor" format="color" />
        <attr name="clockSecondHandStrokeWidth" format="float" />

        <!--Second hand color & stroke width -->
        <attr name="clockNumbersColor" format="color" />
        <attr name="clockNumberSize" format="float" />


### TODO
* Get rid of Deprecated "Handler";
* State hanling via Save&Restore methods;
* Enhance the measuring process;
* Make samples of code;
* Make manual of library usage.
