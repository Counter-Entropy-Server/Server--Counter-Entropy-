#!/usr/bin/env ruby
require 'logs_controller.rb'

class DevicesController < ApplicationController
  
  # GET /devices
  # GET /devices.json
  def index
    @devices = Device.all
    respond_to do |format|
      format.html # index.html.erb
      format.json { render json: @devices }
    end
  end
#http://localhost:3000/devices/setDeviceStateByName?name=light&state=0
def setDeviceStateByName
    @device = Device.find_by_var_name(params[:name])
    @log = Log.create(:address => @device[:address],
                      :state => params[:state],
                      :time => Time.now)
    
    #@device[:state] = '0'
    #@device.update_attributes(:current_state => params[:state])
    #@newdevice = Device.new(@device)
    render json: @log
end
#http://localhost:3000/devices/setDeviceStateByAddress?address=00:00:00:00&state=0
def setDeviceStateByAddress
    @device = Device.find_by_address(params[:address])
    @log = Log.create(:address => @device[:address],
                      :state => params[:state],
                      :time => Time.now)
    #@device[:state] = '0'
    #@device.update_attributes([:current_state] => params[:state])
    #@newdevice = Device.new(@device)
    render json: @log
end

#http://localhost:3000/devices/getDeviceByName?name=door
def getDeviceByName
    @device = Device.find_by_var_name(params[:name])	
    @log = Log.find_by_address(@device[:address])
    render json: @log.last
end
#http://localhost:3000/devices/getDeviceByName?address=00:00:00:00
def getDeviceByAddress
    @device = Device.find_by_address(params[:address])	
    @log = Log.find_by_address(@device[:address])
    render json: @log.last
end
#http://localhost:3000/devices/getRangeByName?name=door&start_time=2012-08-05 18:57:02&end_time=2012-08-05 22:41:02
def getRangeByName
    @device = Device.find_by_var_name(params[:name])	
    @logs = Log.find(:all, :conditions => ['address LIKE ? AND time between ? AND ?',
                                           @device[:address],
                                           params[:start_time],
                                           params[:end_time]])
    render json: @logs
end
#http://localhost:3000/devices/getRangeByName?address=00:00:00:00&start_time=2012-08-05 18:57:02&end_time=2012-08-05 22:41:02
def getRangeByAddress
    @device = Device.find_by_address(params[:address])
    @logs = Log.find(:all, :conditions => ['address LIKE ? AND time between ? AND ?',
                                           @device[:address],
                                           params[:start_time],
                                           params[:end_time]])
    render json: @logs
end

  # GET /devices/1
  # GET /devices/1.json
  def show
    @device = Device.find(params[:id])

    respond_to do |format|
      format.html # show.html.erb
      format.json { render json: @device }
    end
  end





  # GET /devices/new
  # GET /devices/new.json
  def new
    @device = Device.new

    respond_to do |format|
      format.html # new.html.erb
      format.json { render json: @device }
    end
  end

  # GET /devices/1/edit
  def edit
    @device = Device.find(params[:id])
  end

  # POST /devices
  # POST /devices.json
  def create
    @device = Device.new(params[:device])

    respond_to do |format|
      if @device.save
        format.html { redirect_to @device, notice: 'Device was successfully created.' }
        format.json { render json: @device, status: :created, location: @device }
      else
        format.html { render action: "new" }
        format.json { render json: @device.errors, status: :unprocessable_entity }
      end
    end
  end

  # PUT /devices/1
  # PUT /devices/1.json
  def update
    @device = Device.find(params[:id])

    respond_to do |format|
      if @device.update_attributes(params[:device])
        format.html { redirect_to @device, notice: 'Device was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: "edit" }
        format.json { render json: @device.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /devices/1
  # DELETE /devices/1.json
  def destroy
    @device = Device.find(params[:id])
    @device.destroy

    respond_to do |format|
      format.html { redirect_to devices_url }
      format.json { head :no_content }
    end
  end
end