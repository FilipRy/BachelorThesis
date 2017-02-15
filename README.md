# BachelorThesis
My bachelor thesis at TU Wien, August 2015

## Model based testing of cloud based social networks

### Abstract
This thesis presents an application of model based testing to social network, and the
process of mapping a concrete social network to the abstract social network. Based on
this mapping concrete executable test cases can be created from the abstract test cases
derived from the model of the abstract social network.
Firstly I identified the key use cases and components of an abstract social network, for
each use case I identified, which user’s roles should be affected by the execution of the
use case. Based on this information a general model of an abstract social network can be
created. I created the general model using hADL, which allows to define a set of actions
for each collaboration object which can be executed by various human components. I
assume that abstract social network works correctly in the view of the identified use cases.
The functionality provided by the abstract social network matches the functionality of
many existing modern social network, this allows the methodology to be applied to
them. The model is used as input for model based testing to generate abstract test cases.
Having a concrete implementation of a social network and following the process defined
in this thesis, the abstract test cases are mapped to the concrete ones, which can be
executed on the concrete social network. For the concrete social network, I used my
own social network running on Cloud Platform by Google. Based on the results of the
testing the tester can state whether the concrete social network matches the abstract
social network.
