# traffic-counter

### Requirement:

An automated traffic counter sits by a road and counts the number of cars that go past. Every half-hour the counter
outputs the number of cars seen and resets the counter to zero. You are part of a development team that has been asked
to implement a system to manage this data.

### Program Output:

The program will output:

* The number of cars seen in total
* A sequence of lines where each line contains a date (in yyyy-mm-dd format) and the number of cars seen on that day (
  eg. 2016-11-23 289) for all days listed in the input file.
* The top 3 half hours with most cars, in the same format as the input file
* The 1.5 hour period with least cars (i.e. 3 contiguous half hour records)

### Run the app as uber jar:

```
cd $PROJECT_PATH
sbt root/clean root/test root/assembly
java -jar target/scala-2.13/trafficcounter.jar src/main/resources/data/
```

### Run the app with Docker:
```
docker build -t traffic-counter -f docker/Dockerfile .
docker run --rm traffic-counter src/main/resources/data/
```


### Build/Deploy the app (Github Action):

```
see .github/workflow/traffic-counter.yml
```
