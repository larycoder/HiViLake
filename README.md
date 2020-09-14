# HiViLake
A simple datalake system following the philosophy: simple, flexible and replacable.

# Motivation
This is my internship project for building the data lake of USTH. But for now, it can be considered as a storage management solution.

From the beginning, the system architect following the kylo deployment design ([link](https://kylo.io)) since this is only open source that I found out. That is a good example for anyone who first time approach the "data lake" concept. They arrange the system in miro-service style with the heart is **Apache Nifi** ([image](https://github.com/Teradata/kylo/blob/master/docs/latest/architecture.adoc)). This is a simple architect leveraging the apache eco-system tool to manage data. Moreover, the **Apache Nifi** is the data flow automation which fits to Kylo philosophy.

However, this system depends too much on **Apache Nifi** and **Apach Hadoop**. They allow connecting directly their service server (thinkbig-services) into Nifi and Hadoop throught out JDBC or Thrifft. It is not bad and even improves performance of whole system. But I do not like that design, stick too deep with some framework and technology leading to hard replace and extent the system (violate my third philosophy). It can be fixed by appending a wrapper layer between services and storage system. This wrapper layer will define a set of API that all services can use and it keep the lower communication role such as with Spark and Hadoop. This layer, although reduces the performance, is a perfect solution to increase the scalability of whole system.

In the time I do my internship, the Kylo was seem out to be abandoned, and refactor their code is not an easy option. Therefore, I decided to design my own system with that idea. It will not be exptected to accomplish, but I hope it can be a good extensibility system.

# Design
Since my architecture design experience is little to nothing. I try to forcus on the simple of system. And it will be like:

                            +---------------------------+
                            |                           |
                            |                           |
                            |          API Layer        |
                            |                           |
                            |                           |
                            +------------+--------------+
                                         |
                                         v
                            +------------+--------------+
                            |                           |
                            |                           |
                            |       Utility Layer       |
                            |                           |
                            |                           |
                            +------------+--------------+
                                         |
                                         v
                            +------------+--------------+
                            |                           |
                            |                           |
                            |       Storage Layer       |
                            |                           |
                            |                           |
                            +---------------------------+

For better understand about them, please visit my report ([link](https://github.com/larycoder/HiViLake/tree/master/doc/Report/PDF)).

- Report 4.1 : a general architecture design of system.
- Report 5 : a detail feature design and explanation.
- Report 7 and its sub-revision : a class diagram design.

# Community
My system (for this moment) can not reach enough factor to be considered as the data lake. Instead, in my point of view, it is a framework or concept to design and develop a functional data lake. Its core is **Storage layer** and all higher layer will depend on the core api to build up. Therefore, I will be very happy if anyone want to join with me in this project to build up a good open-source framework data lake.

The futher document about project will be completed and update through time.

(2020-09-14)
