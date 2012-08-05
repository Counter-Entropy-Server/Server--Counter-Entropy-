class Device < ActiveRecord::Base
  attr_accessible :address, :current_state, :var_name
end
