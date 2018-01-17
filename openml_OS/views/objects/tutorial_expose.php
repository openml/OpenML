<div class="bs-docs-tutorial">
<div id="exposeCarousel" class="carousel" style="margin-bottom:0px">
  <!-- Carousel items -->
  <div class="carousel-inner">
    <div class="active item">
      <img src="img/tutorial/ExposeTutorial.001.png" alt="">
        <div class="carousel-caption">
            <p>Describing machine learning experiments</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.002.png" alt="">
        <div class="carousel-caption">
            <p>To automatically organize and store machine learning experiments in a transparent way, there are a few things we need to know</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.003.png" alt="">
        <div class="carousel-caption">
            <p>These are details about exactly <b>how</b> the experiment was run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.004.png" alt="">
        <div class="carousel-caption">
            <p>about the setup of the algorithms (e.g., parameter settings)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.005.png" alt="">
        <div class="carousel-caption">
            <p>about the used implementations (e.g., code and version info)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.006.png" alt="">
        <div class="carousel-caption">
            <p style="text-align:right">and general names for machine learning techniques</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.007.png" alt="">
        <div class="carousel-caption">
            <p>Let's start simple: take a dataset, run algorithm, get a model</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.008.png" alt="">
        <div class="carousel-caption">
            <p>Or even simpler: <u>data</u> goes in, <u>run</u>, <u>data</u> comes out</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.009.png" alt="">
        <div class="carousel-caption">
            <p>Data can have different structures: datasets, evaluations, models, and predictions are all stored differently</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.010.png" alt="">
        <div class="carousel-caption">
            <p>E.g., <u>evaluations</u> are linked to known evaluation functions and their implementations</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.011.png" alt="">
        <div class="carousel-caption">
            <p>evaluations can also be labeled, e.g. to store confusion matrices or per-class evaluation measures</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.012.png" alt="">
        <div class="carousel-caption">
            <p>Data has a source: it is linked to the process that generated it (although it can be unknown)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.013.png" alt="">
        <div class="carousel-caption">
            <p>What happens in a run, is described in a <u>setup</u></p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.014.png" alt="">
        <div class="carousel-caption">
            <p>A <u>setup</u> is a plan of what we want to do with the input data, a <u>run</u> is one execution of that plan</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.015.png" alt="">
        <div class="carousel-caption">
            <p>Setups are deterministic, they should uniquely define the run (although runtimes may vary)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.016.png" alt="">
        <div class="carousel-caption">
            <p>A run is executed on a specific machine (or cluster)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.017.png" alt="">
        <div class="carousel-caption">
            <p>All this defines a run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.018.png" alt="">
        <div class="carousel-caption">
            <p>It can have multiple inputs and outputs</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.019.png" alt="">
        <div class="carousel-caption">
            <p>We also keep some meta-data for future reference</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.020.png" alt="">
        <div class="carousel-caption">
            <p>Finally, a run can be part of an experiment (more on that later)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.021.png" alt="">
        <div class="carousel-caption">
            <p>Setups can be hierarchical</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.022.png" alt="">
        <div class="carousel-caption">
            <p>E.g., it could be a cross-validation procedure with an underlying learning algorithm, or a workflow with many operators</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.023.png" alt="">
        <div class="carousel-caption">
            <p>Actually, runs can be hierarchical too</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.024.png" alt="">
        <div class="carousel-caption">
            <p>Every sub-run then points to a sub-setup</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.025.png" alt="">
        <div class="carousel-caption">
            <p>But it often suffices to store the overall run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.026.png" alt="">
        <div class="carousel-caption">
            <p>There are different types of setups: from single functions or algorithms to entire workflows or experiments</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.027.png" alt="">
        <div class="carousel-caption">
            <p>And one can be a component of the other</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.028.png" alt="">
        <div class="carousel-caption">
            <p>E.g., a function can be part of an algorithm setup, and many algorithms can belong to a workflow</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.029.png" alt="">
        <div class="carousel-caption">
            <p>Algorithms can also have sub-algorithms, and workflows can have sub-workflows</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.030.png" alt="">
        <div class="carousel-caption">
            <p>Setups can thus be very simple (a single algorithm) but also very complex (a complex workflow or ensemble learner)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.031.png" alt="">
        <div class="carousel-caption">
            <p>To make sense of complex setups, it helps to assign certain <u>roles</u> to subcomponents</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.032.png" alt="">
        <div class="carousel-caption">
            <p>E.g., an algorithm can be a learner in a cross-validation algorithm, or a kernel function can be a kernel in a learning algorithm</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.033.png" alt="">
        <div class="carousel-caption">
            <p>Next, setups have <u>input settings</u>: they assign fixed values to a setup’s parameters</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.034.png" alt="">
        <div class="carousel-caption">
            <p>If an input value is not fixed in a setup, it must have at least have a default value it can be set to (more on that later)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.035.png" alt="">
        <div class="carousel-caption">
            <p>Moreover, setups always include the specific <u>implementation</u> of the algorithm, function, ... that is to be run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.036.png" alt="">
        <div class="carousel-caption">
            <p>To conclude, setups will always point to specific implementations, may have specific input settings, and may be part of larger setups</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.037.png" alt="">
        <div class="carousel-caption">
            <p>Example: a 10-fold cross-validation setup (random seed 1) using an SVM learning algorithm with an RBF kernel function</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.038.png" alt="">
        <div class="carousel-caption">
            <p>When run, it will generate an evaluation</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.039.png" alt="">
        <div class="carousel-caption">
            <p>And that evaluation will be linked to every detail in the setup</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.040.png" alt="">
        <div class="carousel-caption">
            <p><u>Implementations</u> are executables of certain algorithms, functions or workflows</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.041.png" alt="">
        <div class="carousel-caption">
            <p>Implementations are (uniquely) named, versioned, and should include a download URL and run guidelines</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.042.png" alt="">
        <div class="carousel-caption">
            <p>as well as their library (if any), library version, operating system and programming language</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.043.png" alt="">
        <div class="carousel-caption">
            <p>Implementations typically require a set of inputs and can generate one or more outputs</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.044.png" alt="">
        <div class="carousel-caption">
            <p>They define an implementation’s <i>signature</i>: its name, and the names and datatypes of its inputs and outputs</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.045.png" alt="">
        <div class="carousel-caption">
            <p>this signature can vary between different versions of the implementation</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.046.png" alt="">
        <div class="carousel-caption">
            <p><u>Inputs</u> can be parameters or any other required input (e.g. files), they always have a name and data type</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.047.png" alt="">
        <div class="carousel-caption">
            <p>They can also be given a default value, and possibly a value range or min/max bounds, among others</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.048.png" alt="">
        <div class="carousel-caption">
            <p>Input names are arbitrary, but we can also add a general name to make them more interpretable</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.049.png" alt="">
        <div class="carousel-caption">
            <p><u>Outputs</u> only have a name and data type</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.050.png" alt="">
        <div class="carousel-caption">
            <p></p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.051.png" alt="">
        <div class="carousel-caption">
            <p>Next, we also need to store entire <u>workflows</u></p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.052.png" alt="">
        <div class="carousel-caption">
            <p>Workflows are defined at implementation level, with <u>connections</u> between various implementations</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.053.png" alt="">
        <div class="carousel-caption">
            <p><u>Connections</u> belong to a workflow, and link a source implementation to a target implementation</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.054.png" alt="">
        <div class="carousel-caption">
            <p>The source and target implementations can be algorithms, but also (sub)workflows</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.055.png" alt="">
        <div class="carousel-caption">
            <p>A connection links a specific source port to a specific target port, a port is either an input or output</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.056.png" alt="">
        <div class="carousel-caption">
            <p>Example: a workflow including an algorithm and subworkflow, the connections belong to the top-level workflow</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.057.png" alt="">
        <div class="carousel-caption">
            <p>Changing the structure or components of a workflow yields a new version of that workflow</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.058.png" alt="">
        <div class="carousel-caption">
            <p>The <u>workflow setup</u> further refines the workflow with input settings</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.059.png" alt="">
        <div class="carousel-caption">
            <p>Finally, a workflow can also be part of an algorithm: it would be an editable ‘inner’ workflow of the algorithm</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.060.png" alt="">
        <div class="carousel-caption">
            <p>Data and implementations can have <u>qualities</u>: measurable properties of the data or algorithm</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.061.png" alt="">
        <div class="carousel-caption">
            <p>E.g.</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.062.png" alt="">
        <div class="carousel-caption">
            <p>Qualities use uniform names and must have clear descriptions</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.063.png" alt="">
        <div class="carousel-caption">
            <p>One more thing: experiments. Experiments are a type of setup, and can thus be run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.064.png" alt="">
        <div class="carousel-caption">
            <p>They also have an implementation: the software that controls the experiment</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.065.png" alt="">
        <div class="carousel-caption">
            <p>They have a main setup (an algorithm or workflow), whose settings will be varied in the experiment</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.066.png" alt="">
        <div class="carousel-caption">
            <p>The settings to be varied are expressed as <u>experiment variables</u></p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.067.png" alt="">
        <div class="carousel-caption">
            <p>They have a name and type (i.e., dependent or independent variable), a range of values, and possibly a default value</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.068.png" alt="">
        <div class="carousel-caption">
            <p>Value ranges are expressed as (labeled) tuples</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.069.png" alt="">
        <div class="carousel-caption">
            <p>The experiment design defines how different variables are varied, e.g., <b>fullFactorial</b> will generate all possible combinations </p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.070.png" alt="">
        <div class="carousel-caption">
            <p>Example: here, we vary the parameters of an algorithm and evaluate it on multiple datasets</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.071.png" alt="">
        <div class="carousel-caption">
            <p>Setups can be concrete (all settings are fixed) or an abstract template (some settings depend on input variables)</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.072.png" alt="">
        <div class="carousel-caption">
            <p>Experiments turn an abstract setup into many concrete setups, which can be run</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.073.png" alt="">
        <div class="carousel-caption">
            <p>Finally, we store some metadata: the author, any literature reference, any hypotheses that are tested, conclusions, or a general description</p>
        </div>
    </div>
    <div class="item">
      <img src="img/tutorial/ExposeTutorial.074.png" alt="">
    </div>






  </div>
  <!-- Carousel nav -->
  <a class="carousel-control left" href="#exposeCarousel" data-slide="prev">&lsaquo;</a>
  <a class="carousel-control right" href="#exposeCarousel" data-slide="next">&rsaquo;</a>
</div>
</div>
