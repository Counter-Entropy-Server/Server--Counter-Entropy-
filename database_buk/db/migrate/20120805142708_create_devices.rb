class CreateDevices < ActiveRecord::Migration
  def change
    create_table :devices do |t|
      t.string :var_name
      t.string :address
      t.string :current_state

      t.timestamps
    end
  end
end
