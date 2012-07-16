class CreateDevices < ActiveRecord::Migration
  def change
    create_table :devices do |t|
      t.string :number
      t.string :name
      t.string :address

      t.timestamps
    end
  end
end
