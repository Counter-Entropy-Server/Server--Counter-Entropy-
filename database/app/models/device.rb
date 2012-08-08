class Device < ActiveRecord::Base
  attr_accessible :address, :var_name
  validates :var_name, :uniqueness => true
end
