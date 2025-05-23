
process foo1 {
    debug true
    input:
    path x
    path y
    script:
    """
    echo "FOO1: ${x}; ${y}"
    """
}

process foo2 {
    debug true
    input:
    path x
    path y
    script:
    """
    echo "FOO2: ${x}; ${y}"
    """
}

process foo3 {
    debug true
    input:
    path x
    path y
    shell:
    '''
     echo "FOO3: !{x}; !{y}"
    '''
}

process foo4 {
    debug true
    input:
    path x
    path y
    script:
    template("$baseDir/task-escape-path.sh")
}

workflow {
    f1 = file('file AA.txt')
    ch = channel.fromPath(['file1.txt', 'file2.txt', 'fil BB.txt']).collect()
    foo1(f1,ch)
    foo2(f1,ch)
    foo3(f1,ch)
    foo4(f1,ch)
}
