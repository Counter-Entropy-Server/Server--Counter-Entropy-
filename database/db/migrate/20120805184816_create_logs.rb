class CreateLogs < ActiveRecord::Migration
  def change
    create_table :logs do |t|
      t.string :address
      t.string :state
      t.datetime :time

      t.timestamps
    end
  end
end
