      <h2><?php echo $this->name; ?></h2>
      <table class="table table-striped data_overview_table>">
        <thead>
          <tr>
            <?php foreach( $this->columns as $key ): ?>
              <td><?php echo $key; ?></td>
            <?php endforeach; ?>
          </tr>
        </thead>
        <tbody>

        </tbody>
      </table>
