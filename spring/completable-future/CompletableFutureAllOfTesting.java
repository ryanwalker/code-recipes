package com.keap.subscriptionmanagement.infrastructure.service.allof;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAllOfTesting {

  private static final ExecutorService executorService = Executors.newCachedThreadPool();

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    futures();
    System.out.println(
        "*** NOTE*** The process hangs for awhile after the previous method call. Something is not giving up the resources I think");
  }

  private static void futures() throws InterruptedException, ExecutionException {
    CompletableFuture<FutureResult> future1 =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                System.out.println("Sleeping for 2 second...");
                TimeUnit.SECONDS.sleep(2);
                return new FutureResult(
                    new RuntimeException("This is the error that happened"),
                    "oh dear, i have bombed");
              } catch (Exception e) {
                return new FutureResult(new RuntimeException(e), "I was interrupted");
              }
            },
            executorService);

    CompletableFuture<FutureResult> future2 =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                System.out.println("Sleeping for 5 second...");
                TimeUnit.SECONDS.sleep(5);
                return new FutureResult(null, "All good");
              } catch (Exception e) {
                return new FutureResult(
                    new RuntimeException(e), "Exception has occurred, catch it and go");
              }
            },
            executorService);

    CompletableFuture<FutureResult> future3 =
        CompletableFuture.supplyAsync(
            () -> {
              try {
                System.out.println("Sleeping for 8 second...");
                TimeUnit.SECONDS.sleep(8);
                return new FutureResult(null, "All good");
              } catch (Exception e) {
                return new FutureResult(
                    new RuntimeException(e), "Exception has occurred, catch it and go");
              }
            },
            executorService);

    CompletableFuture.allOf(future1, future2, future3).get();

    if (future1.get().isFailure()) {
      System.out.println(
          "Failed: " + future1.get().isFailure() + ", " + future1.get().getMessage());
      System.out.println(future1.get().getException());
    }

    System.out.println("Failed: " + future2.get().isFailure());
    System.out.println("Failed: " + future3.get().isFailure());
  }

  static class FutureResult {

    private final Exception exception;
    private final String message;

    public FutureResult(Exception exception, String message) {
      this.exception = exception;
      this.message = message;
    }

    public boolean isFailure() {
      return exception != null;
    }

    public Optional<Exception> getException() {
      if (exception != null) {
        return Optional.ofNullable(exception);
      } else {
        return Optional.empty();
      }
    }

    public String getMessage() {
      return message;
    }
  }
}

