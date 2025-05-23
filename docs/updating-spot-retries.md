(spot-retries-page)=

# Spot Instance failures and retries

This page describes changes in how Nextflow handles Spot Instance failures and retries on AWS and Google Cloud, the impact of those changes, and how to configure spot retry behavior for your pipelines. These changes apply to Nextflow 24.10 and later.

## Retry behavior

Up to version 24.10, Nextflow would silently retry Spot Instance failures up to `5` times when using AWS Batch or Google Batch. These retries were controlled by cloud-specific configuration parameters (e.g., `aws.batch.maxSpotAttempts`) and happened in cloud infrastructure without explicit visibility to Nextflow.

<h3>Before Nextflow 24.10</h3>

By default, Nextflow would instruct AWS and Google to automatically retry jobs lost to Spot reclamation up to `5` times. Retries were handled by the cloud provider _within_ a Nextflow task. It was often unclear that tasks were restarted as there was no explicit message. Task runtimes and associated cloud costs were increased because they included the runtime of the reclaimed and retried tasks. Due to the high likelihood of reclamation before completion, long-running tasks running on Spot Instances frequently required retries, leading to inefficient allocation of resources and higher costs.

<h3>After Nextflow 24.10</h3>

The default Spot reclamation retry setting changed to `0` on AWS and Google. By default, no _internal_ retries are attempted on these platforms. Spot reclamations lead to an immediate failure, exposed to Nextflow in the same way as other generic failures (for example, returning `exit code 1` on AWS). Nextflow treats these failures like any other job failure, unless a retry strategy is configured.

## Impact on existing workflows

If you rely on silent Spot retries (the previous default behavior), you may now see more tasks fail with the following characteristics:

- **AWS**: Generic failure with `exit code 1`. You may see messages indicating the host machine was terminated.

- **Google**: Spot reclamation typically produces a specific code, but is now surfaced as a recognizable task failure in Nextflow logs.

Since the default for Spot retries is now `0`, you must actively enable a retry strategy if you want Nextflow to handle reclaimed Spot Instances automatically.

## Possible actions

There are four possible actions.

### Do nothing

If you do not configure anything, you will observe more pipeline failures when Spot Instances are reclaimed. This approach provides clearer visibility into failures. Failed tasks can be re-run with the `-resume` option. However, frequent task reclamation may lead to a higher failure rate and each retry requires manual intervention.

:::{note}
If you resume the pipeline using the resume option, it will pick up at the point the pipeline was interrupted and start with a retry of that task.
:::

### Re-enable Spot retries

You can re-enable Spot retries for each provider in your Nextflow config:

```groovy
aws.batch.maxSpotAttempts = 5
google.batch.maxSpotAttempts = 5
```

The above example sets the maximum number of Spot retries to `5` for both AWS and Google.

### Make Spot failures visible and retry them

You can set `maxRetries` to enable Nextflow-level retries for any failure:

```groovy
process.maxRetries = 5
```

The above example sets retries to `5` for any failures across all providers.

### Use Fusion Snapshots

*This option is only available for AWS Batch.*

If you have long-running tasks where progress lost due to Spot reclamation is costly, consider [Fusion Snapshots](https://docs.seqera.io/fusion/guide/snapshots) (if supported by your environment). Fusion Snapshots allow you to resume a partially completed task on a new machine if a Spot Instance is reclaimed, thereby reducing wasted compute time.

Key features of Fusion Snapshots:

- Jobs do not need to start from scratch after reclamation.
- Especially useful for tasks that take many hours or days to complete.
- May significantly reduce costs and run times in high-reclamation environments.

See [Fusion Snapshots for AWS Batch](https://docs.seqera.io/fusion/guide/snapshots) for more information.

## Best practices

Best practices for Spot Instance failures and retries:

- **Evaluate job duration**: If your tasks are very long (multiple hours or more), Spot Instances can cause repeated interruptions. Consider using on-demand instances or Fusion Snapshots.

- **Set sensible retry limits**: If you enable Spot retries, choose a retry count that balances the cost savings of Spot usage against the overhead of restarting tasks.

- **Monitor logs and exit codes**: Failures due to Spot reclamation will now appear in Nextflow logs. Monitor failures and fine-tune your strategy.

- **Consider partial usage of Spot**: Some workflows may mix on-demand instances for critical or long tasks and Spot Instances for shorter, less critical tasks. This can optimize cost while minimizing wasted compute time.
