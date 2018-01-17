
        <div class="page-header">
          <h2 id="dev-start">OpenML <i class="fa fa-heart"></i> Open Source</h2>
        </div>
	OpenML is an open source project, <a href="https://github.com/organizations/openml">hosted on GitHub</a>. We welcome everybody to help improve OpenML, and make it more useful for everyone.
  If you want to integrate your own machine learning tools with OpenML, <a href="guide/api">check out the available APIs</a>.
<p>
        <i class="fa fa-heart" style="color:red;"></i> We always <a href='https://github.com/openml/OpenML/wiki/How-to-contribute'>love to welcome new contributers</a>, and will gladly help you in any way possible.
</p>

        <h2 id="dev-repos">GitHub repo's</h2>
        <p>You can find relevant code in the corresponding GitHub repositories. Please also post issues in the relevant issue tracker.</p>
        <a href="https://github.com/openml/OpenML"><i class="fa fa-github fa-lg"></i> OpenML Core</a> - Everything done by the OpenML server. This includes dataset feature calculations and server-side model evaluations.<br><br>
        <a href="https://github.com/openml/website"><i class="fa fa-github fa-lg"></i> Website</a> - The website and REST API<br><br>
        <a href="https://github.com/openml/java"><i class="fa fa-github fa-lg"></i> Java API</a> - The Java API and Java-based plugins<br><br>
        <a href="https://github.com/openml/r"><i class="fa fa-github fa-lg"></i> R API</a> - The OpenML R package<br><br>
        <a href="https://github.com/openml/python"><i class="fa fa-github fa-lg"></i> Python API</a> - The Python API

        <h3 id="dev-wiki">GitHub wiki</h3>
        <p>The <a href="https://github.com/openml/OpenML/wiki"> GitHub Wiki</a> contains more information on how to set up your environment to work on OpenML locally, on the structure of the backend and frontend, and working documents.</p>

        <h3 id="dev-database">Database snapshots</h3>
        <p>Everything uploaded to OpenML is available to the community. The nightly snapshot of the public database contains all experiment runs, evaluations and links to datasets, implementations and result files. In SQL format (gzipped). You can also download the <a href="https://www.openml.org/img/expdbschema2.png">Database schema</a>.</p>
        <a href="downloads/ExpDB_SNAPSHOT.sql.gz" class="btn btn-primary"><i class="fa fa-cloud-download fa-lg"></i> Nightly database SNAPSHOT</a><br><br>

	      <p>If you want to work on the website locally, you'll also need the schema for the 'private' database with non-public information.</p>
        <a href="downloads/openml.sql.gz" class="btn btn-primary"><i class="fa fa-cloud-download fa-lg"></i> Private database schema</a>

        <h3 id="dev-wiki">Legacy Resources</h3>
        <p>OpenML is always evolving, but we keep hosting the resources that were used in prior publications so that others may still build on them.</p>
        <p><i class="fa fa-fw fa-database"></i> The <a href="downloads/ExpDB2012.sql.gz">experiment database</a> used in <a href="http://link.springer.com/article/10.1007%2Fs10994-011-5277-0">Vanschoren et al. (2012) Experiment databases. Machine Learning 87(2), pp 127-158</a>. You'll need to import this database (we used MySQL) to run queries. The database structure is described in the paper. Note that most of the experiments in this database have been rerun using OpenML, using newer algorithm implementations and stored in much more detail.</p>
        <p><i class="fa fa-fw fa-share-alt"></i> The <a href="downloads/expose.owl">Exposé ontology</a> used in the same paper, and described in more detail <a href="https://lirias.kuleuven.be/bitstream/123456789/273222/1/sokd10.pdf">here</a> and <a href="http://kt.ijs.si/janez_kranjc/dmo_jamboree/Expose.pdf">here</a>. Exposé is used in designing our databases, and we aim to use it to export all OpenML data as Linked Open Data.</p>
