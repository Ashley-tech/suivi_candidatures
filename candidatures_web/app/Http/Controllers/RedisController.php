<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Redis;

class RedisController extends Controller
{
    public function setKey(string $key, Request $request){
        $redis = app()->make('redis');

        $value = $request->value;
        if (is_array($value) || is_object($value)) {
            $value = json_encode($value, JSON_UNESCAPED_UNICODE);
        }

        $redis->set($key, $value);
        if ($request->filled('ttl')) {
            $redis->expire($key, $request->ttl);
        }
    }

    public function addNewKey(Request $request){
        $redis = app()->make('redis');

        $value = $request->value;
        if (is_array($value) || is_object($value)) {
            $value = json_encode($value, JSON_UNESCAPED_UNICODE);
        }

        $redis->setnx($request->key, $value);
        if ($request->filled('ttl')) {
            $redis->expire($request->key, $request->ttl);
        }
    }

    public function getAllKeys(){
        $redis = app()->make('redis');
        return $redis->keys('*');
    }

    public function getAllKeyValues(){
        $redis = app()->make('redis');
        $keys = $redis->keys('*');
        $result = [];
        foreach ($keys as $key) {
            $result[$key] = $redis->get($key);
        }
        return $result;
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
