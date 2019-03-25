Example of how to use `java.util.concurrent.ExecutorService`
=
Calculating factorial using:
- single thread
- multiple threads via `ExecutorService.submit(Callable<T>)`
- multiple threads via `ExecutorService.invokeAll(Collection<Callable<T>>)`

## Results

### Single threaded
19.935 sec

### Multiple threads

#### Intel Core i5-4210M CPU (2.60GHz)

|Threads|submit|invokeAll|
|-------|------|---------|
|2|7.635|7.310|
|3|5.589|4.664|
|4|3.707|3.543|
