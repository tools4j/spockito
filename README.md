# tools4j-spockito
Java JUnit runner for parameterized tests where the test cases are defined in a table-like
manner. The @Unroll annotation has been inspired the groovy framework Spock.

 
#### Examples
###### Bla
```java
final MeanVarianceSampler sampler = new MeanVarianceSampler();

double mean, var, stdDev;

sampler.add(1);
sampler.add(2.5);
sampler.add(3.22);
sampler.add(-6.72);
mean = sampler.getMean();
var = sampler.getVariance();
stdDev = sampler.getStdDev();

sampler.remove(2.5);
mean = sampler.getMean();
var = sampler.getVariance();
stdDev = sampler.getStdDev();

sampler.replace(3.22, 4.22);
mean = sampler.getMean();
var = sampler.getVariance();
stdDev = sampler.getStdDev();
```

#### More Information
* [MIT License](https://github.com/tools4j/spockito/blob/master/LICENSE)
