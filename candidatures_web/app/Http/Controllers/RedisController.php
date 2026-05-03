<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Redis;

class RedisController extends Controller
{
    public function setKey(Request $request){
        $redis = app()->make('redis');
        $redis->set($request->key, $request->value);
        $redis->expire($request->key, $request->ttl);
    }

    public function addNewKey(Request $request){
        $redis = app()->make('redis');
        $redis->setnx($request->key, $request->value);
        $redis->expire($request->key, $request->ttl);
    }

    public function getAllKeys(){
        $redis = app()->make('redis');
        return $redis->keys('*');
    }

    public function getValue(Request $request){
        $redis = app()->make('redis');
        return $redis->get($request->key);
    }

    public function deleteKey(Request $request){
        $redis = app()->make('redis');
        $redis->del($request->key);
    }
}
