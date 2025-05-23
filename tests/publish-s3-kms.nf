
params.outdir = 'results'

process my_process {
    publishDir params.outdir

    input:
    val(param)

    output:
    file("HELLO.tsv")

    script:
    """
    echo "Hello, world" > HELLO.tsv
    """
}

workflow {
  channel.of(1) | my_process
}
