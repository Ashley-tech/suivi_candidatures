<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Redis;

class RedisController extends Controller
{
    public function setKey($key,$value,$ttl){
        $redis = app()->make('redis');
        $redis->set($key, $value);
        $redis->expire($key, $ttl);
    }

    public function getAllKeys(){
        $redis = app()->make('redis');
        return $redis->keys('*');
    }

    public function getValue($key){
        $redis = app()->make('redis');
        return $redis->get($key);
    }

    public function deleteKey($key){
        $redis = app()->make('redis');
        $redis->del($key);
    }
}
