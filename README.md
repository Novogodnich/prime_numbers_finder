# Prime numbers finder
This is a utility which performs various operations on prime numbers

## Features
This program can find prime numbers in diapason or find prime factors of a number.

 - Asynchronized(faster) finding of numbers
 - Tested on very big numbers(from 1 000 000 000)
 - Tests before compile that checks that program works normally
 - Big numbers support(max is $2^{68719476736}$ in standard finding and 9223372036854775807 in prime factors finding)

## Speed

Tested on i5-12600.

**These are approximate results.**

Finding prime numbers:

| **Start** | **End**       | **Time in ms** |
|-----------|---------------|----------------|
| 1         | 1 000 000     | 117            |
| 1         | 10 000 000    | 901            |
| 1         | 100 000 000   | 12 157         |
| 1 | 1 000 000 000 | 392 547       |

Finding prime factors:

| **Number**       | **Time in ms** |
|---------------|----------------|
| 7 953 024 | 2      |
| 9 548 632 487    | 14       |
| 5 473 285 149 625   | 96         |
| 4 831 510 341 851 253 | 1861       |


## Why this program?
I know that this program has many more efficient alternatives.

This program is specified on prime numbers. It means that **all updates of it will be concentrated on their**.

Other programs often focus on functionality that you do not need.